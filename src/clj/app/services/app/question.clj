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
        {page    :page
         perpage :perpage
         :or     {page    1
                  perpage 10}} page-params
        condition (dissoc params :offset :limit)
        condition (if (empty? condition) :all condition)
        data
        (if (:id uinfo)
          (db/execute!
           (hsql/format
            {:select    [:a.*
                         [[:case [:not= :b.id nil] 1 :else 0] "user_focus"]]
             :from      [[:v_question :a]]
             :left-join [[:focus :b] [:and
                                      [:= :a.id :b.item_id]
                                      [:= :b.type "question"]
                                      [:= :b.user_id (:id uinfo)]]]
             :order-by  [[:a.id :desc]]
             :offset    (* (dec page) perpage)
             :fetch     perpage}))
          (db/find-by-keys :v_question condition
                           {:order-by [[:id :desc]]
                            :offset   (* (dec page) perpage)
                            :limit    perpage}))]
    {:code 0
     :msg "success"
     :data data}))

(defn get-model [uinfo id]
  (log/info "uinfo = " uinfo)
  (let [focus  (db/find-one-by-keys :focus {:user_id (:id uinfo)
                                            :item_id id
                                            :type    "question"})
        answer (db/find-one-by-keys :answer {:user_id     (:id uinfo)
                                             :question_id id})]
    {:code 0
     :msg  "success"
     :data {:focus_id  (:id focus)
            :answer_id (:id answer)}}))

(defn create-model [uinfo params]
  (log/info "uinfo = " uinfo)
  (log/info "params = " params)
  (jdbc/with-transaction [tx conn]
    (let [sqlmap          {:insert-into :question,
                           :values      [(merge
                                          params
                                          {:unverified_modify_count 1
                                           :unverified_modify       ""
                                           :user_id                 (:id uinfo)})]}
          result          (jdbc/execute-one! tx (hsql/format sqlmap)
                                             {:return-keys true
                                              :builder-fn  rs/as-unqualified-lower-maps})

          sqlmap-approval {:insert-into :approval
                           :values      [{:item_id (:id result)
                                          :type    "question"
                                          :user_id (:id uinfo)
                                          :data    (cheshire/generate-string  params)}]}
          result2         (jdbc/execute-one! tx (hsql/format sqlmap-approval)
                                             {:return-keys true
                                              :builder-fn  rs/as-unqualified-lower-maps})]

      (log/info "result = " result)
      (log/info "result2= " result2)
      {:code 0
       :msg  "success"})))
  ; {:code 0
  ;  :msg "success"})

(defn edit-model [uinfo id params]
  (log/info "uinfo = " uinfo)
  (log/info "params = " params)
  (jdbc/with-transaction [tx conn]
    (let [sqlmap {:update :question,
                  :set    {:unverified_modify_count [:+ :unverified_modify_count :1]}
                  :where  [:and [:= :user_id (:id uinfo)]
                           [:= :id id]]}]
      (jdbc/execute-one! tx (hsql/format sqlmap))

      (let [sqlmap {:update :approve,
                    :set    {:data      (cheshire/generate-string params)
                             :create_at [:raw "now()"]
                             :status    0}
                    :where  [:and [:= :user_id (:id uinfo)]
                             [:= :item_id id]
                             [:= :type "question"]]}
            result (jdbc/execute-one! tx (hsql/format sqlmap)
                                      {:return-keys true
                                       :builder-fn  rs/as-unqualified-lower-maps})]
        (log/info "result = " result))))
  {:code 0
   :msg  "success"})

(defn remove-model [uinfo id]
  (let [model (db/get-by-id :question id)]
    (check-service/check-must-exist model "must exists")

    (check-service/check-own-entity uinfo model "must own entity")

    (let [result (db/delete! :question {:id id})]
      (log/info "result = " result)))
  {:code 0
   :msg  "success"})
