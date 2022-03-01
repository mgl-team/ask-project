(ns app.services.add.topic
  (:require
   [honey.sql :as hsql]
   [java-time :as time]
   [next.jdbc :as jdbc]
   [next.jdbc.sql :as sql]
   [next.jdbc.result-set :as rs]
   [clojure.tools.logging :as log]
   [cuerdas.core :as str]
   [cheshire.core :as cheshire]
   [app.db.core :as db :refer [conn]]
   [app.config :refer [env]]
   [app.services.check :as check-service]
   [app.middleware.exception :as exception]))

(defn get-models [uinfo pid]
  (log/info " pid = " pid)
  (let [data (db/find-by-keys :topic  {:question_id pid}
              {:order-by [[:id :desc]]})]
    {:code 0
     :msg "success"
     :data data}))

(defn create-model [uinfo params]
  (log/info "uinfo = " uinfo)
  (log/info "params = " params)

  (check-service/check-admin uinfo "must admin")

  (db/insert! :topic (assoc params :user_id (:id uinfo)))
  {:code 0
   :msg  "success"})

(defn edit-model [uinfo id params]
  (log/info "uinfo = " uinfo)
  (log/info "params = " params)

  (check-service/check-admin uinfo "must admin")

  (let [model (db/get-by-id :topic id)]
    (check-service/check-must-exist model "must exists")

    (db/update! :topic params {:id id}))
  {:code 0
   :msg  "success"})

(defn remove-model [uinfo id]

  (check-service/check-admin uinfo "must admin")

  (let [model (db/get-by-id :topic id)]
    (check-service/check-must-exist model "must exists")

    (db/delete! :topic {:id id}))

  {:code 0
   :msg  "success"})
