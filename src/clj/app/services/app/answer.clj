(ns app.services.app.answer
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

(defn get-models [pid]
  (log/info " pid = " pid)
  (let [data (db/find-by-keys :v_answer  {:question_id pid}
              {:order-by [[:id :desc]]})]
    {:code 0
     :msg "success"
     :data data}))

(defn create-model [uinfo pid params]
  (log/info "uinfo = " uinfo " pid = " pid)
  (log/info "params = " params)
  (jdbc/with-transaction [tx conn]
    (sql/insert! tx :answer
      (assoc params :question_id pid
                    :user_id (:id uinfo)))
    (let [entity (sql/get-by-id tx :question pid
                   {:builder-fn rs/as-unqualified-lower-maps})
          user-answer (sql/find-by-keys tx :answer {:user_id (:id uinfo)
                                                    :question_id pid})]
      (sql/update! tx :question
        (merge
          { :updated_at (time/local-date-time)
            :answer_count (inc (:answer_count entity))}
          (if (empty? user-answer)
            { :answer_user (inc (:answer_user entity))}))
        {:id pid})))
  {:code 0
   :msg "success"})

(defn edit-model [uinfo pid id params]
  (log/info "uinfo = " uinfo)
  (log/info "params = " params)
  (let [model (db/get-by-id :answer id)]
    (check-service/check-must-exist model "must exists")

    (check-service/check-own-entity uinfo model "must own entity")

    (db/update! :answer params {:id id}))
  {:code 0
   :msg "success"})

(defn remove-model [uinfo id]
  (let [model (db/get-by-id :answer id)]
    (check-service/check-must-exist model "must exists")

    (check-service/check-own-entity uinfo model "must own entity")

    (log/warn "model = " (select-keys model [:content]))
    (jdbc/with-transaction [tx conn]
      (sql/delete! tx :answer {:id id})

      (let [json-value (cheshire/generate-string (select-keys model [:content]))
            sqlmap-approval {:insert-into :approval
                             :values [{:item_id id
                                       :type "answer"
                                       :user_id (:id uinfo)
                                       :data json-value}]}

            result (jdbc/execute-one! tx (hsql/format sqlmap-approval)
                       {:return-keys true
                        :builder-fn rs/as-unqualified-lower-maps})

            sqlmap {:insert-into :approval_log,
                    :values [{:status 1
                              :approve_id (:id result)
                              :data json-value
                              :approve_user_id 0}]}]

        (jdbc/execute-one! tx (hsql/format sqlmap)))

      (let [sqlmap {:update :question
                    :set {:answer_count  [:- :answer_count 1]}
                    :where [:= :id (:question_id model)]}]
        (jdbc/execute-one! tx (hsql/format sqlmap)))))
  {:code 0
   :msg "success"})
