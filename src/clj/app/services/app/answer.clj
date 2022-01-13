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

(defn get-models [uinfo pid]
  (log/info "uinfo = " uinfo)
  (let [data (db/find-by-keys :v_answer id
              {:order-by [[:id :desc]]})]
    {:code 0
     :msg "success"
     :data data}))

(defn create-model [uinfo pid params]
  (log/info "uinfo = " uinfo)
  (log/info "params = " params)
  (jdbc/with-transaction [tx conn]
    (sql/insert! tx :answer
      (assoc params :question_id pid
                    :user_id (:id uinfo)))
    (let [entity (sql/get-by-id tx :question pid)
          user-answer (sql/find-by-keys tx :answer {:user_id (:id uinfo)
                                                    :question_id pid})]
      (sql/update! tx :question
        (merge
          { :updated_at (time/local-date-time)
            :answer_count (inc (:answer_count entity))}
          (if (empty? user-answer)
            { :answer_user (inc (:answer_user entity))})))))
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

    (jdbc/with-transaction [tx conn]
      (sql/delete! tx :answer {:id id})

      (let [sqlmap {:insert-into :approval_log,
                    :values [{:status 1
                              :data (select-keys model [:contentc])
                              :approve_user_id 0}]}

            ;
            sqlmap-approval {:insert-into :approval
                             :values [{:item_id (:id result)
                                       :type "answer"
                                       :user_id (:id uinfo)
                                       :data (cheshire/generate-string  params)}]}]
        (jdbc/execute-one! tx (hsql/format sqlmap)))))
  {:code 0
   :msg "success"})
