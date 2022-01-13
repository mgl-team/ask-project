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
    (let [map-value {:item_id pid
                     :type pname
                     :vote_value (:vote_value params)
                     :user_id (:id uinfo)}

          entity (sql/find-by-keys tx :vote map-value
                    {:builder-fn rs/as-unqualified-lower-maps})

          parent-count-name (if (= 1 (:vote_value params))
                              :agree_count
                              :against_count)

          [sql-fn parent-count-fn] (if (empty? entity)
                                     [sql/insert! :+]
                                     [sql/delete! :-])
          sqlmap {:update pname
                  :set {parent-count-name  [parent-count-fn parent-count-name 1]}
                  :where [:= :id pid]}]


      (sql-fn tx :vote map-value)

      (jdbc/execute-one! tx (hsql/format sqlmap))))

  {:code 0
   :msg "success"})
