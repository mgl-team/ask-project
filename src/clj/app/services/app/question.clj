(ns app.services.app.question
  (:require
   [honey.sql :as hsql]
   [next.jdbc.sql :as sql]
   [next.jdbc.result-set :as rs]
   [java-time :as time]
   [clojure.tools.logging :as log]
   [cuerdas.core :as str]
   [app.db.core :as db :refer [conn]]
   [app.config :refer [env]]
   [app.services.check :as check-service]
   [app.middleware.exception :as exception]))

(defn get-models [uinfo params]
  (log/info "uinfo = " uinfo)
  (let [page-params (select-keys params [:page :perpage])
        {page :page, perpage :perpage :or {page 1, perpage 10}} page-params
        condition (dissoc params :offset :limit)
        condition (if (empty? condition) :all condition)
        data (db/find-by-keys :v_question condition
              {:order-by [[:id :desc]]
               :offset (* (dec page) perpage)
               :limit perpage})]
    {:code 0
     :msg "success"
     :data data}))

(defn create-model [uinfo params]
  (log/info "uinfo = " uinfo)
  (log/info "params = " params)
  (let [params (clojure.set/rename-keys params {:content :question_content :detail :question_detail})
        result (db/insert! :question (assoc params :user_id (:id uinfo)))]
    (log/info "result = " result)
    {:code 0
     :msg "success"}))
  ; {:code 0
  ;  :msg "success"})
