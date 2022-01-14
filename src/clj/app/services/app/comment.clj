(ns app.services.app.comment
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

(defn get-models [uinfo pname pid]
  (log/info "uinfo = " uinfo)
  (let [data (db/find-by-keys :v_comment
               {:item_id pid :type pname}
               {:order-by [[:id :desc]]})]
    {:code 0
     :msg "success"
     :data data}))

(defn create-model [uinfo pname pid params]
  (log/info "uinfo = " uinfo)
  (log/info "params = " params)
  (jdbc/with-transaction [tx conn]
    (sql/insert! tx :comments
      (assoc params :item_id pid
                    :type pname
                    :user_id (:id uinfo)))
    (let [sqlmap {:update (keyword pname)
                  :set {:comment_count  [:+ :comment_count 1]}
                  :where [:= :id pid]}]
      (jdbc/execute-one! tx (hsql/format sqlmap))))

  {:code 0
   :msg "success"})

(defn edit-model [uinfo pid id params]
  (log/info "uinfo = " uinfo)
  (log/info "params = " params)
  (let [model (db/get-by-id :comments id)]
    (check-service/check-must-exist model "must exists")

    (check-service/check-own-entity uinfo model "must own entity")

    (db/update! :comments params {:id id}))
  {:code 0
   :msg "success"})

(defn remove-model [uinfo pname pid id]
  (log/info "uinfo = " uinfo " pname " pname " pid " pid " id " id)
  (let [model (db/get-by-id :comments id)]
    (check-service/check-must-exist model "must exists")

    (check-service/check-own-entity uinfo model "must own entity")

    (jdbc/with-transaction [tx conn]
      (sql/delete! tx :comments {:id id})

      (let [sqlmap {:update (keyword pname)
                    :set {:comment_count  [:- :comment_count 1]}
                    :where [:= :id pid]}]
        (jdbc/execute-one! tx (hsql/format sqlmap)))

      (let [json-value (cheshire/generate-string (select-keys model [:message]))
            sqlmap-approval {:insert-into :approval
                             :values [{:item_id id
                                       :type "comment"
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

        (jdbc/execute-one! tx (hsql/format sqlmap)))))
  {:code 0
   :msg "success"})
