(ns app.services.user.info
  (:require
   [honeysql.core :as hsql]
   [next.jdbc.sql :as sql]
   [next.jdbc.result-set :as rs]
   [java-time :as time]
   [clojure.tools.logging :as log]
   [cuerdas.core :as str]
   [app.db.core :refer [conn]]
   [app.config :refer [env]]
   [app.services.sms :as sms-service]
   [app.services.check :as check-service]
   [app.middleware.exception :as exception]))

(defn user-info [uinfo]
  (let [entity (sql/find-by-id conn :users (:id uinfo)
                 {:builder-fn rs/as-unqualified-lower-maps})]

    ;; check entity is not empty.
    (check-service/check-must-exist entity "user does not exist!")

    (let [data
          (-> entity
            (select-keys [:username :email :mobile])
            (assoc :password (if (:encrypted_password entity) true false)))]
      {:code 0
       :msg "success"
       :data data})))


(defn set-username [uinfo params]
  (let [entity (sql/find-by-id conn :users (:id uinfo)
                 {:builder-fn rs/as-unqualified-lower-maps})]

    ;; check entity is not empty.
    (check-service/check-must-exist entity "user does not exist!")

    ;; set username
    (let [result (sql/update! conn :users (:id uinfo) {:username   (:username params)
                                                       :updated_at (hsql/raw "now()")})]
      (log/info "set-username result = " result))

    {:code 0
     :msg  "success"}))

(defn set-email [uinfo params]
  ;; check code TODO

  ;; set username
  (let [result (sql/update! conn :users (:id uinfo) {:email      (:email params)
                                                     :updated_at (hsql/raw "now()")})]
    (log/info "set-email result = " result))

  {:code 0
   :msg  "success"})

(defn set-mobile [uinfo params]
  ;; check code
  (sms-service/check-sms params)

  ;; set username
  (let [result (sql/update! conn :users (:id uinfo) {:mobile     (:mobile params)
                                                     :updated_at (hsql/raw "now()")})]
    (log/info "set-mobile result = " result))

  {:code 0
   :msg  "success"})
