(ns app.services.app.question
  (:require
   [honey.sql :as hsql]
   [java-time :as time]
   [next.jdbc :as jdbc]
   [next.jdbc.result-set :as rs]
   [clojure.tools.logging :as log]
   [cuerdas.core :as str]
   [cheshire.core :as cheshire]
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

(defn get-model [uinfo id]
  (log/info "uinfo = " uinfo)
  (let [entity (db/get-by-id :v_)]))

(defn create-model [uinfo params]
  (log/info "uinfo = " uinfo)
  (log/info "params = " params)
  (jdbc/with-transaction [tx conn]
    (let [sqlmap {:insert-into :question,
                  :values [{:unverified_modify_count  1
                            :user_id (:id uinfo)}]}
          result (jdbc/execute-one! tx (hsql/format sqlmap)
                   {:return-keys true
                    :builder-fn rs/as-unqualified-lower-maps})

          sqlmap-approval {:insert-into :approval
                           :values [{:item_id (:id result)
                                     :type "question"
                                     :user_id (:id uinfo)
                                     :data (cheshire/generate-string  params)}]}
          result2 (jdbc/execute-one! tx (hsql/format sqlmap-approval)
                    {:return-keys true
                     :builder-fn rs/as-unqualified-lower-maps})]

      (log/info "result = " result)
      (log/info "result2= " result2)
      {:code 0
       :msg "success"})))
  ; {:code 0
  ;  :msg "success"})

(defn edit-model [uinfo id params]
  (log/info "uinfo = " uinfo)
  (log/info "params = " params)
  (jdbc/with-transaction [tx conn]
    (let [sqlmap {:update :question,
                  :set {:unverified_modify_count  [:+ :unverified_modify_count :1]}
                  :where [:and [:= :user_id (:id uinfo)]
                               [:= :id id]]}]
      (jdbc/execute-one! tx (hsql/format sqlmap))

      (let [sqlmap {:update :approve,
                    :set {:data (cheshire/generate-string params)
                          :create_at [:raw "now()"]
                          :status 0}
                    :where [:and [:= :user_id (:id uinfo)]
                                 [:= :item_id id]
                                 [:= :type "question"]]}
            result (jdbc/execute-one! tx (hsql/format sqlmap)
                      {:return-keys true
                       :builder-fn rs/as-unqualified-lower-maps})]
        (log/info "result = " result))))
  {:code 0
   :msg "success"})

(defn remove-model [uinfo id]
  (let [model (db/get-by-id :question id)]
    (check-service/check-must-exist model "must exists")

    (check-service/check-own-entity uinfo model "must own entity")

    (let [result (db/delete! :question {:id id})]
      (log/info "result = " result)))
  {:code 0
   :msg "success"})
