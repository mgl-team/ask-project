(ns app.routes.answer
  (:require
   [app.middleware :as middleware]
   [ring.util.http-response :refer :all]
   [spec-tools.data-spec :as ds]
   [app.services.app.answer :as service]
   [app.services.app.report :as report-service]))

(def route
  [["/questions/:id/answers"
    {:swagger {:tags ["answers"]}
     ; :middleware [[middleware/wrap-restricted]]
     :post {:summary "add."
            :middleware [[middleware/wrap-restricted]]
            :parameters {:body {:content string?} :path {:id integer?}}
            :responses {200 {:body {:code int? :msg string?, (ds/opt :errors) any?
                                                           , (ds/opt :data) any?}}}
            :handler (fn [{{body :body {id :id} :path} :parameters  token :identity}]
                       (ok (service/create-model token id body)))}
     :get {:summary "get list."
           :parameters {:path {:id integer?}}
           :responses {200 {:body {:code int? :msg string?, (ds/opt :errors) any?
                                                          , (ds/opt :data) any?}}}
           :handler (fn [{{{id :id} :path} :parameters token :identity}]
                      (ok (service/get-models id)))}}]
   ["/questions/:pid/answers/:id"
    {:swagger    {:tags ["answers"]}
     :middleware [[middleware/wrap-restricted]]
     :put        {:summary    "edit."
                  :parameters {:path {:pid integer? :id integer?}
                               :body {:content string?}}
                  :responses {200 {:body {:code int? :msg string?, (ds/opt :errors) any?
                                                                 , (ds/opt :data) any?}}}
                  :handler    (fn [{{body     :body
                                     {pid :pid id :id} :path} :parameters
                                    token           :identity}]
                                (ok (service/edit-model token pid id body)))}

     :delete     {:summary    "remove."
                  :parameters {:path {:id integer?}}
                  :responses {200 {:body {:code int? :msg string?, (ds/opt :errors) any?
                                                                 , (ds/opt :data) any?}}}
                  :handler    (fn [{{body     :body
                                     {id :id} :path} :parameters
                                    token           :identity}]
                                (ok (service/remove-model token id)))}}]
   ["/answers/:id/report"
    {:swagger    {:tags ["answers"]}
     :middleware [[middleware/wrap-restricted]]
     :post {:summary "add."
            :parameters {:body {:reason string?} :path {:id integer?}}
            :responses {200 {:body {:code int? :msg string?, (ds/opt :errors) any?
                                                           , (ds/opt :data) any?}}}
            :handler (fn [{{body :body {id :id} :path} :parameters  token :identity}]
                       (ok (report-service/report token "answer" id body)))}}]])
