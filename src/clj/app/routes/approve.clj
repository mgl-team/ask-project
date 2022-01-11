(ns app.routes.approve
  (:require
   [app.middleware :as middleware]
   [ring.util.http-response :refer :all]
   [spec-tools.data-spec :as ds]
   [app.services.add.question :as service]))

(def route
  [["/approve"
    {:swagger {:tags ["approve"]}
     :middleware [[middleware/wrap-restricted]]
     :get {:summary "get list."
           :parameters {:query {(ds/opt :page) int?, (ds/opt :perpage) int?}}
           :responses {200 {:body {:code int? :msg string?, (ds/opt :errors) any?
                                                          , (ds/opt :data) any?}}}
           :handler (fn [{{:keys [identity]} :session {:keys [query]} :parameters}]
                      (ok (service/get-models identity)))}}]

   ["/approve/:id"
    {:swagger    {:tags ["approve"]}
     :middleware [[middleware/wrap-restricted]]
     :post        {:summary    "edit."
                   :parameters {:path {:id integer?}
                                :body {(ds/opt :reason) string?}}
                   :responses {200 {:body {:code int? :msg string?, (ds/opt :errors) any?
                                                                  , (ds/opt :data) any?}}}
                   :handler    (fn [{{body     :body
                                      {id :id} :path} :parameters
                                     token           :identity}]
                                 (ok (service/edit-model token id body)))}}]])
