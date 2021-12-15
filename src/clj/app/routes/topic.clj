(ns app.routes.topic
  (:require
   [app.middleware :as middleware]
   [ring.util.http-response :refer :all]
   [spec-tools.data-spec :as ds]))

(def route
  [["/topics"
    {:swagger {:tags ["topics"]}
     ; :middleware [[middleware/wrap-restricted]]
     :post {:summary "add."
            :parameters {:body {}}
            :responses {200 {:body {:success boolean? :msg string? (ds/opt :data) any?}}}
            :handler (fn [{{body :body} :parameters {:keys [identity]} :session}]
                       (ok))}
     :get {:summary "get list."
           :parameters {:query {(ds/opt :page) int?, (ds/opt :perpage) int?}}
           :responses {200 {:body {:success boolean? :msg string? (ds/opt :data) any?}}}
           :handler (fn [{{:keys [identity]} :session {:keys [query]} :parameters}]
                      (ok))}}]
   ["/topics/:id"
    {:swagger    {:tags ["topics"]}
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
