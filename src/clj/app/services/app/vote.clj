(ns app.services.app.vote
  (:require
   [honey.sql :as hsql]
   [next.jdbc :as jdbc]
   [next.jdbc.sql :as sql]
   [next.jdbc.result-set :as rs]
   [clojure.tools.logging :as log]
   [app.db.core :refer [conn]]
   [app.services.check :as check-service]
   [app.middleware.exception :as exception]))

(defn vote [uinfo pname pid params]
  (jdbc/with-transaction [tx conn]
    (let [map-value                {:item_id    pid
                                    :type       pname
                                    :vote_value (:vote_value params)
                                    :user_id    (:id uinfo)}

          entity                   (sql/find-by-keys tx :vote map-value
                                                     {:builder-fn rs/as-unqualified-lower-maps})

          ;; ----------------------
          opposite-map-value       (assoc map-value :vote_value (if (= 1 (:vote_value params))
                                                                  -1
                                                                  1))
          opposite-entity          (sql/find-by-keys tx :vote opposite-map-value
                                                     {:builder-fn rs/as-unqualified-lower-maps})

          opposite-count-name      (if (= 1 (:vote_value params))
                                     :against_count
                                     :agree_count)
          ;; ----------------------
          
          parent-count-name        (if (= 1 (:vote_value params))
                                     :agree_count
                                     :against_count)

          [sql-fn parent-count-fn] (if (empty? entity)
                                     [sql/insert! :+]
                                     [sql/delete! :-])
          sqlmap                   {:update (keyword pname)
                                    :set    (merge
                                             {parent-count-name [parent-count-fn parent-count-name 1]}
                                             (if-not (empty? opposite-entity)
                                               {opposite-count-name [:- opposite-count-name 1]}))
                                    :where  [:= :id pid]}]

      (when-not (empty? opposite-entity)
        (sql/delete! tx :vote opposite-map-value))

      (sql-fn tx :vote map-value)

      (jdbc/execute-one! tx (hsql/format sqlmap))))

  {:code 0
   :msg  "success"})
