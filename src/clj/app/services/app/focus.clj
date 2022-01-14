(ns app.services.app.focus
  (:require
   [honey.sql :as hsql]
   [next.jdbc :as jdbc]
   [next.jdbc.sql :as sql]
   [next.jdbc.result-set :as rs]
   [clojure.tools.logging :as log]
   [app.db.core :refer [conn]]
   [app.services.check :as check-service]
   [app.middleware.exception :as exception]))

(defn focus [uinfo pname pid]
  (jdbc/with-transaction [tx conn]
    (let [map-value {:item_id pid
                     :type pname
                     :user_id (:id uinfo)}

          entity (sql/find-by-keys tx :focus map-value
                    { :columns [:id]
                      :builder-fn rs/as-unqualified-lower-maps})

          [sql-fn parent-count-fn] (if (empty? entity)
                                     [sql/insert! :+]
                                     [sql/delete! :-])
          sqlmap {:update (keyword pname)
                  :set {:focus_count  [parent-count-fn :focus_count 1]}
                  :where [:= :id pid]}]


      (sql-fn tx :focus map-value)

      (jdbc/execute-one! tx (hsql/format sqlmap))))

  {:code 0
   :msg "success"})
