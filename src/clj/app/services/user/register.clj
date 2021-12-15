(ns app.services.user.register
  (:require
   [honeysql.core :as hsql]
   [next.jdbc.sql :as sql]
   [java-time :as time]
   [clojure.tools.logging :as log]
   [cuerdas.core :as str]
   [clj-http.client :as client]
   [buddy.hashers :as hashers]
    ; [app.services.http-request :as http-service]
   [app.db.core :as db :refer [conn]]
   [app.config :refer [env]]
   [app.services.sms :as sms]
   [app.middleware.exception :as exception]))

(defn- check-before-register [user]
  ;; check password
  ; (if (not= (:password user) (:password-confirmation user))
  ;   (throw (ex-info "check" {:type ::exception/check
  ;                            :msg  "password not match!"})))

  ;; check mobile exist
  (let [entity (db/find-by-keys :users {:mobile (:mobile user)})]
    (check-service/check-must-not-exist entity "mobile already exists"))

  ;; check user exists
  ; (if (and (:username user)
  ;          (empty? (sql/find-by-keys :users {:username (:username user)})))
  ;   (throw (ex-info "check" {:type ::exception/check
  ;                            :msg  "user already exists!"}))))

  ;; check code
  (sms/check-sms {:phone (:mobile user) :code (:code user)}))

(defn register [user]
  (check-before-register user)
  (let [{:keys [mobile]}                         user
        ; password                                 (hashers/derive (:password user))
        model                                    (-> user)
        ; (select-keys [:username :email :mobile]) (assoc :encrypted_password password)
        result                                   (db/insert! :users model)]

    {:code 0
     :msg  "success"
     :data result}))
