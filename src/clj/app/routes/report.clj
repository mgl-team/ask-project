(ns app.routes.report
  (:require
   [app.middleware :as middleware]
   [ring.util.http-response :refer :all]
   [spec-tools.data-spec :as ds]
   [app.services.app.report :as service]))

(def route
  [["/reports/answers"
    {:swagger {:tags ["reports"]}
     :middleware [[middleware/wrap-restricted]]
     :get     {:summary    "get list."
               :parameters {:path {:id integer?}}
               :responses  {200 {:body {:code            int?
                                        :msg             string?
                                        (ds/opt :errors) any?
                                        (ds/opt :data)   any?}}}
               :handler    (fn [{{{id :id} :path} :parameters
                                 token            :identity}]
                             (ok (service/get-models token id)))}}]])
   
