(ns app.services.app.report
  (:require
   [app.db.core :as db]
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
