(ns app.services.add.report
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

(declare report-no report-yes)

(defn get-models [uinfo]
  (check-service/check-admin uinfo "must admin")

  (let [data (->> (db/find-by-keys :v_report :all {:order-by [[:reported_at :desc]]
                                                   :fetch    10})
                  (map #(assoc % :data (cheshire/parse-string (:data %) true))))]
    {:code 0
     :msg  ""
     :data data}))

(defn edit-model [uinfo id params]
  (check-service/check-admin uinfo "must admin")

  (if (and (not= 1 (:status params))
           (empty? (:reason params)))
    (exception/ex-throw "reason"))

  (if (= 1 (:status params))
    (report-yes uinfo id)
    (report-no  uinfo id (:reason params))))


(defn report-yes [uinfo id]
  (jdbc/with-transaction [tx conn]
    (let [entity (sql/get-by-id tx :v_report id
                                {:builder-fn rs/as-unqualified-lower-maps})]

      (sql/update! tx :report {:status 1} {:id id})
      (sql/update! tx (keyword (:type entity)) {:status -1} {:id (:item_id entity)})

      (let [json-value      (cheshire/generate-string (select-keys entity [:message :reason]))
            sqlmap-approval {:insert-into :approval
                             :values      [{:data    json-value
                                            :type    "report"
                                            :item_id (:id entity)
                                            :user_id (:reported_user_id entity)
                                            :status  1}]}

            result          (jdbc/execute-one! tx (hsql/format sqlmap-approval)
                                               {:return-keys true
                                                :builder-fn  rs/as-unqualified-lower-maps})

            sqlmap          {:insert-into :approval_log,
                             :values      [{:status          1
                                            :approve_id      (:id result)
                                            :data            json-value
                                            :approve_user_id (:id uinfo)}]}]
        (jdbc/execute-one! tx (hsql/format sqlmap))))

    {:code 0
     :msg  ""}))

(defn report-no [uinfo id reason]
  (jdbc/with-transaction [tx conn]
    (let [entity (sql/get-by-id tx :v_report id
                                {:builder-fn rs/as-unqualified-lower-maps})]
      (sql/update! tx :report {:status -1} {:id id})

      (let [json-value      (cheshire/generate-string (select-keys entity [:message :reason]))
            sqlmap-approval {:insert-into :approval
                             :values      [{:data    json-value
                                            :type    "report"
                                            :item_id (:id entity)
                                            :user_id (:reported_user_id entity)
                                            :status  -1}]}

            result          (jdbc/execute-one! tx (hsql/format sqlmap-approval)
                                               {:return-keys true
                                                :builder-fn  rs/as-unqualified-lower-maps})

            sqlmap          {:insert-into :approval_log,
                             :values      [{:status          -1
                                            :data            json-value
                                            :approve_id      id
                                            :approve_user_id (:id uinfo)
                                            :reason          reason}]}]
        (jdbc/execute-one! tx (hsql/format sqlmap))))

    {:code 0
     :msg  ""}))
