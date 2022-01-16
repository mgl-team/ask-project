(ns app.services.app.draft
  (:require
   [app.db.core :as db :refer [conn]]
   [honey.sql :as hsql]
   [next.jdbc.sql :as sql]
   [next.jdbc :as jdbc]
   [next.jdbc.result-set :as rs]
   [app.services.check :as check-service]))

(defn create-entity [uinfo params]
  (jdbc/with-transaction [tx conn]
    (let [{pid :item_id pname :type} params
          map-value {:item_id pid
                     :type pname
                     :user_id (:id uinfo)}

          sqlmap {:update :user_ex
                  :set {:draft_count  [:+ :draft_count 1]}
                  :where [:= :id (:id uinfo)]}]


      (sql/insert! tx :draft (merge params map-value))

      (jdbc/execute-one! tx (hsql/format sqlmap))))

  {:code 0
   :msg "success"})

(defn edit-entity [uinfo id params]
  (let [entity (db/get-by-id :draft id)]
    (check-service/check-must-exist entity "must exists")

    (check-service/check-own-entity uinfo entity "must own entity"))

  (db/update! :draft params {:id id})

  {:code 0
   :msg "success"})

(defn get-entities [uinfo params]
  {:code 0
   :msg "success"
   :data (db/find-by-keys :draft (if empty? :all params) 
           {:order-by [[:id :desc]]})})

(defn remove-entity [uinfo id]
  (jdbc/with-transaction [tx conn]
    (let [entity (sql/get-by-id tx :draft id
                   {:builder-fn rs/as-unqualified-lower-maps})]
      (check-service/check-must-exist entity "must exists")

      (check-service/check-own-entity uinfo entity "must own entity")

      (sql/delete! tx :draft {:id id}))

    (let [sqlmap {:update :user_ex
                  :set {:draft_count  [:- :draft_count 1]}
                  :where [:= :id (:id uinfo)]}]
      (jdbc/execute-one! tx (hsql/format sqlmap))))

  {:code 0
   :msg "success"})
