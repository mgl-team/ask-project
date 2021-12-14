(ns app.services.user.info
  (:require
   [honeysql.core :as hsql]
   [next.jdbc.sql :as sql]
   [java-time :as time]
   [clojure.tools.logging :as log]
   [cuerdas.core :as str]
   [app.db.core :refer [conn]]
   [app.config :refer [env]]
   [app.services.sms :as sms-service]
   [app.middleware.exception :as exception]))

; (defn user-info [uinfo]
;   (let [entity (sql/find-by-id conn :users (:id uinfo))]
;
;     ;; check entity is not empty.
;     (if (empty? entity)
;       (throw (ex-info "check" {:type ::exception/check
;                                :msg  "user does not exist!"})))))

(defn set-username [uinfo params]
  (let [entity (sql/find-by-id conn :users (:id uinfo))]

    ;; check entity is not empty.
    (if (empty? entity)
      (throw (ex-info "check" {:type ::exception/check
                               :msg  "user does not exist!"})))

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
