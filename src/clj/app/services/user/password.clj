(ns app.services.user.password
  (:require
    [honeysql.core :as hsql]
    [next.jdbc.sql :as sql]
    [java-time :as time]
    [clojure.tools.logging :as log]
    [app.config :refer [env]]
    [cuerdas.core :as str]
    [app.middleware.exception :as exception]
    [buddy.hashers :as hashers]
    [buddy.core.hash :as hash]
    [buddy.core.codecs :as codecs]))
    ; [app.services.http-request :as http-service]))

(defn change-password [token params]
  ;; check confirmation
  (if-not (= (:password params) (:password-confirmation params))
    (throw (ex-info "check" {:type ::exception/check :msg "confirmation not match!"})))

  ;; check current password
  (let [entity (sql/find-by-id :users (:id token))]
    (if-not (hashers/check (:current-password params) (:encrypted_password entity))
      (throw (ex-info "check" {:type ::exception/check :msg "password not match!"}))))

  ;; update db
  (sql/update! :users (:id token) {:password      (hashers/derive (:password params))
                                   :updated_at    (hsql/raw "now()")})

  {:code 0 :msg "success"})
