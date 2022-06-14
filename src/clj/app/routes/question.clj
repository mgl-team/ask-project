(ns app.routes.question
  (:require
   [app.middleware :as middleware]
   [ring.util.http-response :refer :all]
   [spec-tools.data-spec :as ds]
   [app.services.app.question :as service]
   [app.services.app.focus :as focus-service]))

(def route
  [["/questions"
    {:swagger {:tags ["questions"]}
     ; :middleware [[middleware/wrap-restricted]]
     :post    {:summary    "add."
               :middleware [[middleware/wrap-restricted]]
               :parameters {:body {:question_content string?
                                   :question_detail  string?}}
               :responses  {200 {:body {:code            int?
                                        :msg             string?
                                        (ds/opt :errors) any?
                                        (ds/opt :data)   any?}}}
               :handler    (fn [{{body :body} :parameters
                                 token        :identity}]
                             (ok (service/create-model token body)))}
     :get     {:summary    "get list."
               :parameters {:query {(ds/opt :page)    int?
                                    (ds/opt :perpage) int?}}
               :responses  {200 {:body {:code            int?
                                        :msg             string?
                                        (ds/opt :errors) any?
                                        (ds/opt :data)   any?}}}
               :handler    (fn [{token           :identity
                                 {:keys [query]} :parameters}]
                             (ok (service/get-models token query)))}}]

   ["/questions/:id"
    {:swagger {:tags ["questions"]}
     ; :middleware [[middleware/wrap-restricted]]
     :put     {:summary    "edit."
               :parameters {:path {:id integer?}
                            :body {(ds/opt :question_content) string?
                                   (ds/opt :question_detail)  string?}}
               :responses  {200 {:body {:code            int?
                                        :msg             string?
                                        (ds/opt :errors) any?
                                        (ds/opt :data)   any?}}}
               :handler    (fn [{{body     :body
                                  {id :id} :path} :parameters
                                 token                        :identity}]
                             (ok (service/edit-model token id body)))}

     :delete  {:summary    "remove."
               :parameters {:path {:id integer?}}
               :responses  {200 {:body {:success       boolean?
                                        :msg           string?
                                        (ds/opt :data) any?}}}
               :handler    (fn [{{body     :body
                                  {id :id} :path} :parameters
                                 token                        :identity}]
                             (ok (service/remove-model token id)))}
     :get     {:summary    "get one."
               :middleware [[middleware/wrap-restricted]]
               :parameters {:path {:id integer?}}
               :responses  {200 {:body {:code            int?
                                        :msg             string?
                                        (ds/opt :errors) any?
                                        (ds/opt :data)   any?}}}
               :handler    (fn [{token            :identity
                                 {{id :id} :path} :parameters}]
                             (ok (service/get-model token id)))}}]
   ["/questions/:id/focus"
    {:swagger {:tags ["questions"]}
     :post    {:summary    "focus."
               :middleware [[middleware/wrap-restricted]]
               :parameters {:path {:id integer?}}
               :responses  {200 {:body {:code            int?
                                        :msg             string?
                                        (ds/opt :errors) any?
                                        (ds/opt :data)   any?}}}
               :handler    (fn [{{{id :id} :path} :parameters
                                 token            :identity}]
                             (ok (focus-service/focus token "question" id)))}}]])
