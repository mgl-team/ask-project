(ns app.services.user.logout
  (:require
    [honeysql.core :as hsql]
    [clojure.tools.logging :as log]
    [app.middleware.exception :as exception]
    [app.db.core :as db :refer [conn]]
    [next.jdbc.sql :as sql]
    [next.jdbc.result-set :as rs]))

(defn logout [token]
  (let [entity (db/get-by-id :users (:id token))]
    ;; check exists
    (check-service/check-must-exist entity "user does not exist!")

    (db/insert! :jwt_blacklist {:jti        (:jti token)
                                :exp        (:exp token)
                                :user_id    (:id entity)
                                :created_at (db/now)
                                :updated_at (db/now)})

    {:code 0
     :msg  "success"}))
