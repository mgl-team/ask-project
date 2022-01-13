(ns app.routes.answer
  (:require
   [app.middleware :as middleware]
   [ring.util.http-response :refer :all]
   [spec-tools.data-spec :as ds]
   [app.services.app.answer :as service]))

(def route
  [["/question/:id/answers"
    {:swagger {:tags ["answers"]}
     ; :middleware [[middleware/wrap-restricted]]
     :post {:summary "add."
            :parameters {:body {:content string?} :path {:id integer?}}
            :responses {200 {:body {:success boolean? :msg string? (ds/opt :data) any?}}}
            :handler (fn [{{body :body} :parameters {id :id} :path token :identity}]
                       (ok (service/create-model token id body)))}
     :get {:summary "get list."
           :parameters {:path {:id integer?}}
           :responses {200 {:body {:success boolean? :msg string? (ds/opt :data) any?}}}
           :handler (fn [{{id :id} :path token :identity}]
                      (ok (service/get-models id)))}}]
   ["/answers/:id"
    {:swagger    {:tags ["answers"]}
     ; :middleware [[middleware/wrap-restricted]]
     :put        {:summary    "edit."
                  :parameters {:path {:id integer?}
                               :body {}}
                  :responses  {200 {:body {:success       boolean?
                                           :msg           string?
                                           (ds/opt :data) any?}}}
                  :handler    (fn [{{body     :body
                                     {id :id} :path} :parameters
                                    {:keys [identity]}           :session}]
                                (ok))}

     :delete     {:summary    "remove."
                  :parameters {:path {:id integer?}}
                  :responses  {200 {:body {:success       boolean?
                                           :msg           string?
                                           (ds/opt :data) any?}}}
                  :handler    (fn [{{body     :body
                                     {id :id} :path} :parameters
                                    {:keys [identity]}           :session}]
                                (ok))}
     :get        {:summary    "get one."
                  :parameters {:path {:id integer?}}
                  :responses  {200 {:body {:success       boolean?
                                           :msg           string?
                                           (ds/opt :data) any?}}}
                  :handler    (fn [{{:keys [identity]} :session
                                    {{id :id} :path}   :parameters}]
                                (ok))}}]])
