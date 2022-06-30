(ns app.routes.search
  (:require
   [app.middleware :as middleware]
   [ring.util.http-response :refer :all]
   [spec-tools.data-spec :as ds]
   [app.services.app.search :as service]))

(def route
  [["/search/questions"
    {:swagger {:tags ["searchs"]}
     ; :middleware [[middleware/wrap-restricted]]
     :get {:summary "get list."
           :parameters {:query {:search string?}}
           :responses {200 {:body {:code int? :msg string?, (ds/opt :errors) any?
                                                          , (ds/opt :data) any?}}}
           :handler (fn [{{params :query} :parameters token :identity}]
                      (ok (service/search-questions token params)))}}]
   ["/search/questions-you-type"
    {:swagger {:tags ["searchs"]}
     ; :middleware [[middleware/wrap-restricted]]
     :get {:summary "get list."
           :parameters {:query {:content string?}}
           :responses {200 {:body {:code int? :msg string?, (ds/opt :errors) any?
                                                          , (ds/opt :data) any?}}}
           :handler (fn [{{params :query} :parameters token :identity}]
                      (ok (service/search-questions-you-type token params)))}}]
   ["/search/articles"
    {:swagger {:tags ["searchs"]}
     ; :middleware [[middleware/wrap-restricted]]
     :get {:summary "get list."
           :parameters {:query {:content string?}}
           :responses {200 {:body {:code int? :msg string?, (ds/opt :errors) any?
                                                          , (ds/opt :data) any?}}}
           :handler (fn [{{params :query} :parameters token :identity}]
                      (ok (service/search-articles token params)))}}]
   ["/search/articles-you-type"
    {:swagger {:tags ["searchs"]}
     ; :middleware [[middleware/wrap-restricted]]
     :get {:summary "get list."
           :parameters {:query {:content string?}}
           :responses {200 {:body {:code int? :msg string?, (ds/opt :errors) any?
                                                          , (ds/opt :data) any?}}}
           :handler (fn [{{params :query} :parameters token :identity}]
                      (ok (service/search-articles-you-type token params)))}}]])
