(ns app.api.qs
  (:require
   [clj-http.client :as client]
   [clj-http.cookies :as cookie]
   [clojure.test :refer :all]
   [cheshire.core :as cheshire]
   [app.db.core :as db]))


(comment
  (require '[app.services.check :as check])
  (require '[app.services.add.question :as service] :reload)
  (require '[clj-http.client :as http])
  (require '[clj-http.cookies :as cookie])
  (require '[cheshire.core :as cheshire])
  (require '[clojure.test :refer :all])

  (require '[app.routes.services :as route] :reload)

  (route/service-routes)

  (user/start)
  (user/stop)


  (service/get-models {:id 11})
  (db/get-by-id :users 11)

  ()

  )