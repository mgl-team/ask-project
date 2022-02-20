(ns app.services.user.logout
  (:require
    [honey.sql :as hsql]
    [clojure.tools.logging :as log]
    [app.middleware.exception :as exception]
    [app.db.core :as db :refer [conn]]
    [app.services.check :as check-service]
    [next.jdbc.sql :as sql]
    [next.jdbc.result-set :as rs]))

(defn logout [token]
  (let [entity (db/get-by-id :users (:id token))]
    ;; check exists
    (check-service/check-must-exist entity "user does not exist!")

    (let [sqlmap {:insert-into [:jwt_blacklist]
                  :values      [{:jti        (:jti token)
                                 :exp        (:exp token)
                                 :user_id    (:id entity)
                                 :created_at [:raw "now"]
                                 :updated_at [:raw "now"]}]}
          result (db/execute! (hsql/format sqlmap))]
      (log/warn "result = " result))

    {:code 0
     :msg  "success"}))
