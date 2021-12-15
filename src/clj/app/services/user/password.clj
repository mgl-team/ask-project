(ns app.services.user.password
  (:require
    [honeysql.core :as hsql]
    [next.jdbc.sql :as sql]
    [next.jdbc.result-set :as rs]
    [java-time :as time]
    [clojure.tools.logging :as log]
    [cuerdas.core :as str]
    [buddy.hashers :as hashers]
    [buddy.core.hash :as hash]
    [buddy.core.codecs :as codecs]
    [app.config :refer [env]]
    [app.db.core :refer [conn]]
    [app.middleware.exception :as exception]
    [app.services.sms :as sms-service]))

(defn change-password [token params]
  ;; check confirmation
  (if-not (= (:password params) (:password-confirmation params))
    (throw (ex-info "check" {:type ::exception/check
                             :msg  "confirmation not match!"})))

  (let [entity (sql/find-by-id :users (:id token)
                  {:builder-fn rs/as-unqualified-lower-maps})]

    ;; check entity is not empty.
    (if (empty? entity)
      (throw (ex-info "check" {:type ::exception/check
                               :msg  "user does not exist!"})))

    ;; check current password
    (if-not (hashers/check (:current-password params) (:encrypted_password entity))
      (throw (ex-info "check" {:type ::exception/check
                               :msg  "password not match!"}))))

  ;; update db
  (sql/update! :users (:id token) {:password   (hashers/derive (:password params))
                                   :updated_at (hsql/raw "now()")})

  {:code 0
   :msg  "success"})

(defn set-password [uinfo params]
  (let [entity (sql/get-by-id conn :users (:id uinfo)
                  {:builder-fn rs/as-unqualified-lower-maps})]

    (if (empty? entity)
      (throw (ex-info "check" {:type ::exception/check
                               :msg  "user does not exist!"})))

    (if-not (= (:password params) (:password-confirmation params))
      (throw (ex-info "check" {:type ::exception/check
                               :msg  "confirmation not match!"})))
    ;; update db
    (sql/update! :users (:id uinfo) {:password   (hashers/derive (:password params))
                                     :updated_at (hsql/raw "now()")})

    {:code 0
     :msg  "success"}))
