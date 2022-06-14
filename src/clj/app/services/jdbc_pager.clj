(ns app.services.jdbc-pager
  (:require
    [clojure.tools.logging :as log]
    [app.middleware.exception :as exception]
    [app.db.core :refer [conn]]
    [next.jdbc :as jdbc]
    [next.jdbc.sql :as sql]
    [next.jdbc.result-set :as rs]
    [honeysql.core :as hsql]))

(defn query [select tables conds opts page-params]
  (let [{page :page, perpage :perpage :or {page 1, perpage 10}} page-params
        offset (* (dec page) perpage)

        total-sqlmap (merge {:select [:%count.*] :from tables :where conds} (or opts {}))
        total (-> (jdbc/execute-one! conn (hsql/format total-sqlmap)) :count)

        total-pages (int (Math/ceil (/ total perpage)))

        sqlmap (merge {:select select :from tables :where conds} (or opts {}) {:limit perpage :offset offset})

        data (if (> total 0)
                (jdbc/execute! conn (hsql/format sqlmap)
                  {:builder-fn rs/as-unqualified-lower-maps})
                [])]
    {:total total-pages
     :page page
     :perpage perpage
     :data data}))

(defn query-with-join [sqlmap page-params]
  (let [{page    :page
         perpage :perpage
         :or     {page    1
                  perpage 10}} page-params
        offset             (* (dec page) perpage)

        total-sqlmap       (dissoc (merge sqlmap {:select [:%count.*]}) :order-by)
        total              (-> (jdbc/execute-one! conn (hsql/format total-sqlmap)) :count)

        total-pages        (int (Math/ceil (/ total perpage)))

        sqlmap (merge sqlmap {:offset (* (dec page) perpage)
                              :fetch perpage})

        data               (if (> total 0)
                             (jdbc/execute! conn (hsql/format sqlmap)
                                            {:builder-fn rs/as-unqualified-lower-maps})
                             [])]
    {:total   total-pages
     :page    page
     :perpage perpage
     :data    data}))
