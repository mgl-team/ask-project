(ns app.services.app.report
  (:require
   [app.db.core :as db]
   [honey.sql :as hsql]
   [app.services.check :as check-service]
   [app.middleware.exception :as exception]))

(defn report [uinfo pname pid params]
  (let [map-value {:item_id pid
                   :type    pname
                   :user_id (:id uinfo)}

        entity    (db/find-by-keys :report map-value
                                   { :columns [:id]})]


    (if-not (empty? entity)
      (exception/ex-throw "already reported"))

    (db/insert! :report (merge params map-value)))

  {:code 0
   :msg  "success"})

(defn get-models [uinfo]
  (let [sqlmap {:select [[:a.item_id :answer_i]
                         :a.status :a.reason :id
                         :a.updated_at
                         :b.content]
                :from [[:report :a]]
                :left-join [[:answer :b] [:and
                                          [:= :b.id :a.item_id]
                                          [:= :a.type "answer"]]]
                :where [:= :a.user_id (:id uinfo)]
                :order-by [[:id :desc]]}

        data (db/execute! (hsql/format sqlmap))]
    {:code 0
     :msg "success"
     :data data}))
