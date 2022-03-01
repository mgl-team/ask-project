(ns app.services.app.article
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

(defn get-models [uinfo params]
  (log/info " uinfo = " uinfo)

  (let [page-params (select-keys params [:page :perpage])
        {page    :page
         perpage :perpage
         :or     {page    1
                  perpage 10}} page-params

        condition (merge
                    (dissoc params :offset :limit :self)
                    (if (:self params) {:user_id (:id uinfo)}))
        condition (if (empty? condition) :all condition)

        data (db/find-by-keys :article    condition
                              {:order-by [[:id :desc]]
                               :offset   (* (dec page) perpage)
                               :limit    perpage})]
    {:code 0
     :msg "success"
     :data data}))

(defn create-model [uinfo params]
  (log/info "uinfo = " uinfo)
  (log/info "params = " params)
  (db/insert! :article
               (assoc params :user_id (:id uinfo)))

  {:code 0
   :msg  "success"})

(defn edit-model [uinfo id params]
  (log/info "uinfo = " uinfo)
  (log/info "params = " params)
  (let [model (db/get-by-id :article id)]
    (check-service/check-must-exist model "must exists")

    (check-service/check-own-entity uinfo model "must own entity")

    (db/update! :article params {:id id}))
  {:code 0
   :msg  "success"})

(defn remove-model [uinfo id]
  (let [model (db/get-by-id :article id)]
    (check-service/check-must-exist model "must exists")

    (check-service/check-own-entity uinfo model "must own entity")

    (db/delete! :article {:id id}))


  {:code 0
   :msg  "success"})


(defn get-model [uinfo id]
  (let [model (db/get-by-id :article id)]
   {:code 0
    :msg  "success"
    :data model}))
