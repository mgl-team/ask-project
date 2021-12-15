(ns app.services.user.logout
  (:require
    [honeysql.core :as hsql]
    [clojure.tools.logging :as log]
    [app.middleware.exception :as exception]
    [next.jdbc.sql :as sql]
    [next.jdbc.result-set :as rs]))

(defn logout [token]
  (let [entity (sql/get-by-id :users (:id token)
                  {:builder-fn rs/as-unqualified-lower-maps})]
    ;; check exists
    (if (empty? entity)
      (throw (ex-info "check" {:type ::exception/check
                               :msg  "user does not exists!"})))

    (sql/insert! :jwt_blacklist {:jti        (:jti token)
                                 :exp        (:exp token)
                                 :user_id    (:id entity)
                                 :created_at (hsql/raw "now()")
                                 :updated_at (hsql/raw "now()")})

    {:code 0
     :msg  "success"}))
