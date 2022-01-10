(ns app.routes.question
  (:require
   [app.middleware :as middleware]
   [ring.util.http-response :refer :all]
   [spec-tools.data-spec :as ds]
   [app.services.app.question :as service]))

(def route
  [["/questions"
    {:swagger {:tags ["questions"]}
     ; :middleware [[middleware/wrap-restricted]]
     :post {:summary "add."
            :middleware [[middleware/wrap-restricted]]
            :parameters {:body {:content string? :detail string?}}
            :responses {200 {:body {:code int? :msg string?, (ds/opt :errors) any?
                                                           , (ds/opt :data) any?}}}
            :handler (fn [{{body :body} :parameters token :identity}]
                       (ok (service/create-model token body)))}
     :get {:summary "get list."
           :parameters {:query {(ds/opt :page) int?, (ds/opt :perpage) int?}}
           :responses {200 {:body {:code int? :msg string?, (ds/opt :errors) any?
                                                          , (ds/opt :data) any?}}}
           :handler (fn [{{:keys [identity]} :session {:keys [query]} :parameters}]
                      (ok (service/get-models identity query)))}}]
   ["/questions/:id"
    {:swagger    {:tags ["questions"]}
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
