(ns app.services.app.thanks
  (:require
   [honey.sql :as hsql]
   [next.jdbc :as jdbc]
   [next.jdbc.sql :as sql]
   [next.jdbc.result-set :as rs]
   [clojure.tools.logging :as log]
   [app.db.core :refer [conn]]
   [app.services.check :as check-service]
   [app.middleware.exception :as exception]))

(defn thanks [uinfo pname pid]
  "thanks = like"
  (jdbc/with-transaction [tx conn]
    (let [map-value                {:item_id pid
                                    :type    pname
                                    :user_id (:id uinfo)}

          entity                   (sql/find-by-keys tx :thanks map-value
                                                     { :columns    [:id]
                                                      :builder-fn rs/as-unqualified-lower-maps})

          [sql-fn parent-count-fn] (if (empty? entity)
                                     [sql/insert! :+]
                                     [sql/delete! :-])
          sqlmap                   {:update (keyword pname)
                                    :set    {:thanks_count [parent-count-fn :thanks_count 1]}
                                    :where  [:= :id pid]}]


      (sql-fn tx :thanks map-value)

      (jdbc/execute-one! tx (hsql/format sqlmap))))

  {:code 0
   :msg  "success"})
