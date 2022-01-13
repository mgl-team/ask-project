(ns app.services.add.question
  (:require
   [honey.sql :as hsql]
   [java-time :as time]
   [next.jdbc.sql :as sql]
   [next.jdbc :as jdbc]
   [next.jdbc.result-set :as rs]
   [clojure.tools.logging :as log]
   [cuerdas.core :as str]
   [cheshire.core :as cheshire]
   [app.db.core :as db :refer [conn]]
   [app.config :refer [env]]
   [app.services.check :as check-service]
   [app.middleware.exception :as exception]))

(declare approve-yes approve-no)

(defn get-models [uinfo]
  (check-service/check-admin uinfo "must admin")

  (let [data (->> (db/find-by-keys :v_add_approve :all {:order-by [[:created_at :desc]] :fetch 10})
                  (map #(assoc % :data (cheshire/parse-string (:data %) true))))]
    {:code 0
     :msg ""
     :data data}))

(defn edit-model [uinfo id params]
  (check-service/check-admin uinfo "must admin")

  (if (and (= 1 (:status params))
           (empty? (:reason params)))
    (exception/ex-throw "reason"))

  (if (= 1 (:status params))
    (approve-yes uinfo id)
    (approve-no  uinfo id (:reason params))))


(defn approve-yes [uinfo id]
  (let [entity (db/get-by-id :approval id)]
    (check-service/check-must-exist entity "must exists")

    (jdbc/with-transaction [tx conn]
      (let [params (cheshire/parse-string (:data entity) true)
            sqlmap {:update :question,
                    :set  (merge params
                            {:unverified_modify_count 0
                             :unverified_modify   ""
                             :update_at [:raw "now()"]})}]

        (jdbc/execute-one! tx (hsql/format sqlmap))

        (let [sqlmap {:insert-into :approval_log,
                      :values [{:status 1
                                :approve_id id
                                :data (:data entity)
                                :approve_user_id (:id uinfo)}]}]
          (jdbc/execute-one! tx (hsql/format sqlmap)))

        (sql/update! tx :approval {:status 1} {:id id})))

    {:code 0
     :msg ""}))

(defn approve-no [uinfo id reason]
  (let [entity (db/get-by-id :approval id)]
    (check-service/check-must-exist entity "must exists")

    (jdbc/with-transaction [tx conn]
      (let [sqlmap {:insert-into :approval_log,
                    :values [{:status 0
                              :data (:data entity)
                              :approve_id id
                              :approve_user_id (:id uinfo)
                              :reason reason}]}]
        (jdbc/execute-one! tx (hsql/format sqlmap))

        (sql/update! tx :approval {:status -1} {:id id})))

    {:code 0
     :msg ""}))
