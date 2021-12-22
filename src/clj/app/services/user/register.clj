(ns app.services.user.register
  (:require
   [honey.sql :as hsql]
   [next.jdbc.sql :as sql]
   [next.jdbc :as jdbc]
   [java-time :as time]
   [clojure.tools.logging :as log]
   [cuerdas.core :as str]
   [clj-http.client :as client]
   [chime.core :as chime]
   [buddy.hashers :as hashers]
    ; [app.services.http-request :as http-service]
   [app.db.core :as db :refer [conn]]
   [app.config :refer [env]]
   [app.services.sms :as sms]
   [app.services.check :as check-service]
   [app.services.token :as token]
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

(defn register [user headers addr]
  (check-before-register user)
  (let [{:keys [mobile]}                         user
        ; password                                 (hashers/derive (:password user))
        model                                    (-> user
                                                   (select-keys [:mobile]))
                                                    ; (assoc :encrypted_password password))
        result                                   (db/insert! :users model)]

    (if (nil? (:id result))
      (exception/ex-throw "user add error!"))

    ;; update statistics record
    (let [sqlmap {:update :users, :set {:sign_in_count      [:+ :sign_in_count 1]
                                        :current_sign_in_at [:raw "now()"]
                                        :current_sign_in_ip addr
                                        :updated_at         [:raw "now()"]
                                        :failed_attempts    0}
                  :where [:= :id (:id result)]}]
      (chime/chime-at [(.plusSeconds (time/instant) 1)]
                      (fn [time]
                        (let [result (db/execute! (hsql/format sqlmap))]
                          (log/info "update statistics record " result)))))

    ;; generate token
    (let [token  (token/jwt-token (:id result))]
      {:code  0
       :token token
       :msg   "success"})))
