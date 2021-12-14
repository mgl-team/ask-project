(ns app.services.user.info
  (:require
   [honeysql.core :as hsql]
   [next.jdbc.sql :as sql]
   [java-time :as time]
   [clojure.tools.logging :as log]
   [cuerdas.core :as str]
   [app.db.core :refer [db]]
   [app.config :refer [env]]
   [app.services.sms :as sms]
   [app.middleware.exception :as exception]))

; (defn user-info [uinfo]
;   (let [entity (sql/find-by-id :users (:id token))]
;
;     ;; check entity is not empty.
;     (if (empty? entity)
;       (throw (ex-info "check" {:type ::exception/check
;                                :msg  "user does not exist!"})))))
