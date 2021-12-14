(ns app.services.user.register
  (:require
    [honeysql.core :as hsql]
    [next.jdbc.sql :as sql]
    [java-time :as time]
    [clojure.tools.logging :as log]
    [app.config :refer [env]]
    [cuerdas.core :as str]
    [app.middleware.exception :as exception]
    [clj-http.client :as client]
    ; [app.services.http-request :as http-service]
    [buddy.hashers :as hashers]))

(defn- check-before-register [user]
  ;; check password
  (if (not= (:password user) (:password-confirmation user))
    (throw (ex-info "check" {:type ::exception/check :msg "password not match!"})))

  ;; check mobile exist
  (if (and (:mobile user)
           (empty? (sql/find-by-keys :users {:mobile (:mobile user)})))
    (throw (ex-info "check" {:type ::exception/check :msg "mobile already exists!"})))

  ;; check user exists
  (if (and (:username user)
           (empty? (sql/find-by-keys :users {:username (:username user)})))
    (throw (ex-info "check" {:type ::exception/check :msg "user already exists!"}))))

  ;; check code TODO

(defn register [user]
  (check-before-register user)
  (let [{:keys [username email mobile]} user
        password (hashers/derive (:password user))
        model (-> user)
              (select-keys [:username :email :mobile])
              (assoc :encrypted_password password)
        result (sql/insert! :users model)]

    {:code 0 :msg "success" :data result}))
