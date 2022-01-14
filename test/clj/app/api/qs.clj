(ns app.api.qs
  (:require
   [clj-http.client :as client]
   [clj-http.cookies :as cookie]
   [clojure.zip :as zip]
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

  (defn nested-zipper [root coll]
   (let [by-parent (group-by :pid coll)]
    (loop [z (zip/zipper some? #(by-parent (:id %)) #(assoc %1 :children %2) (first (by-parent nil)))]
      (if (zip/end? z)
        (zip/root z)
        (recur (zip/next (zip/edit z identity)))))))
  
  (def coll [{:id 0, :parent nil, :name "item-0"}
   {:id 1, :parent 0, :name "item-1"}
   {:id 2, :parent 1, :name "item-2"}
   {:id 3, :parent 2, :name "item-3"}
   {:id 4, :parent 3, :name "item-4"}])
  
  (nested-zipper {:id 0, :parent nil, :name "item-0"} coll)

  )