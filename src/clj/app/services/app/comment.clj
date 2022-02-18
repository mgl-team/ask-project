(ns app.services.app.comment
  (:require
   [honey.sql :as hsql]
   [java-time :as time]
   [next.jdbc :as jdbc]
   [next.jdbc.sql :as sql]
   [next.jdbc.result-set :as rs]
   [clojure.tools.logging :as log]
   [clojure.zip :as zip]
   [cuerdas.core :as str]
   [cheshire.core :as cheshire]
   [app.db.core :as db :refer [conn]]
   [app.config :refer [env]]
   [app.services.check :as check-service]
   [app.middleware.exception :as exception]))

(defn nested-coll [root coll]
  (let [by-parent (group-by :pid coll)]
    (loop [z (zip/zipper some? #(by-parent (:id %)) #(assoc %1 :children %2) (first (by-parent nil)))]
      (if (zip/end? z)
        (zip/root z)
        (recur (zip/next (zip/edit z identity)))))))

(defn get-models [uinfo pname pid]
  (log/info "uinfo = " uinfo)
  (if (:id uinfo)
    (let [sqlmap {:select [:a.*
                           [[:case [:not= :b.id nil] 1 :else 0] "user_like"]]
                  :from [[:v_comment :a]]
                  :left-join [[:thanks :b] [:and
                                            [:= :a.id :b.item_id]
                                            [:= :b.type "comment"]
                                            [:= :b.user_id (:id uinfo)]]]
                  :where [:and [:= :a.item_id pid]
                               [:= :a.type pname]]
                  :order-by [[:id :desc]]}
          data (db/execute! (hsql/format sqlmap))]
      {:code 0
       :msg "success"
       :data data})
    (let [data (concat [{:id 0 :pid nil}] (db/find-by-keys :v_comment
                                            {:item_id pid :type pname}
                                            {:order-by [[:id :desc]]}))
          nested-data (nested-coll {:id 0 :pid nil} data)]
      {:code 0
       :msg "success"
       :data (or (-> nested-data :children) [])})))

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
