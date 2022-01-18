(ns app.services.user.info
  (:require
   [honey.sql :as hsql]
   [next.jdbc.sql :as sql]
   [next.jdbc.result-set :as rs]
   [java-time :as time]
   [clojure.tools.logging :as log]
   [cuerdas.core :as str]
   [app.db.core :as db :refer [conn]]
   [app.config :refer [env]]
   [app.services.sms :as sms-service]
   [app.services.check :as check-service]
   [app.middleware.exception :as exception]))

(defn user-info [uinfo]
  (log/info "uinfo = " uinfo)
  (let [entity (db/get-by-id :users (:id uinfo))]

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
  (let [entity (db/get-by-id :users (:id uinfo))]

    ;; check entity is not empty.
    (check-service/check-must-exist entity "user does not exist!"))

    ;; set username
  (let [sqlmap {:update :users, :set {:username   (:username params)
                                      :updated_at [:raw "now()"]}
                :where [:= :id (:id uinfo)]}
        result (db/execute! (hsql/format sqlmap))]
    (log/warn "result = " result))

  {:code 0
   :msg  "success"})

(defn set-email [uinfo params]
  ;; check code TODO

  ;; set username
  (let [sqlmap {:update :users, :set {:email   (:email params)
                                      :updated_at [:raw "now()"]}
                :where [:= :id (:id uinfo)]}
        result (db/execute! (hsql/format sqlmap))]
    (log/warn "result = " result))

  {:code 0
   :msg  "success"})

(defn set-mobile [uinfo params]
  ;; check code
  (sms-service/check-sms params)

  ;; set username
  (let [sqlmap {:update :users, :set {:mobile   (:mobile params)
                                      :updated_at [:raw "now()"]}
                :where [:= :id (:id uinfo)]}
        result (db/execute! (hsql/format sqlmap))]
    (log/warn "result = " result))


  {:code 0
   :msg  "success"})

(defn check-mobile [params headers addr]
  (let [entity (db/find-one-by-keys :users params)]

    {:code 0
     :msg  "success"
     :data {:mobile (:mobile params) :exists (if (empty? entity) false true)
            :password (if (:encrypted_password entity) true false)}}))

(defn send-code [params headers addr]
  (let [entity (db/find-by-keys :users {:mobile (:mobile params)})]
    (if (= 1 (:direction params))
      (check-service/check-must-not-exist entity "mobile already exists")
      (check-service/check-must-exist entity "mobile not exists")))

  (sms-service/send-sms {:phone (:mobile params)})

  {:code 0
   :msg  "success"})

(defn get-code [params]
  (sms-service/get-code params))
