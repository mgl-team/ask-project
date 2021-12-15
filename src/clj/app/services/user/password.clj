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
    [app.db.core :as db :refer [conn]]
    [app.middleware.exception :as exception]
    [app.services.sms :as sms-service]
    [app.services.check :as check-service]))

(defn change-password [token params]
  ;; check confirmation
  (check-service/check-password-confirmation
    (:password params)
    (:password-confirmation params)
    "confirmation not match!")

  (let [entity (db/get-by-id conn :users (:id token))]

    ;; check entity is not empty.
    (check-service/check-must-exist entity "user does not exist!")

    ;; check current password
    (check-service/check-current-password
      (:current-password params)
      entity
      "password not match!"))

  ;; update db
  (db/update! :users {:id (:id uinfo)} {:password   (hashers/derive (:password params))
                                        :updated_at (db/now)})

  {:code 0
   :msg  "success"})

(defn set-password [uinfo params]
  (let [entity (db/get-by-id :users (:id uinfo))]

    (check-service/check-must-exist entity "user does not exist!")

    (check-service/check-password-confirmation
      (:password params)
      (:password-confirmation params)
      "confirmation not match!")

    ;; update db
    (db/update! :users {:id (:id uinfo)} {:password   (hashers/derive (:password params))
                                          :updated_at (db/now)})

    {:code 0
     :msg  "success"}))
