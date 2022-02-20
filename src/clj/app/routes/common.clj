(ns app.routes.common
  (:require
   [app.middleware :as middleware]
   [ring.util.http-response :refer :all]
   [spec-tools.data-spec :as ds]
   [app.services.app.thanks :as thanks-service]
   [app.services.app.vote :as vote-service]
   [app.services.app.favorite :as favorite-service]
   [app.services.app.comment :as comment-service]
   [app.services.app.draft :as draft-service]
   [app.services.app.report :as report-service]))

(def route
  [["/answers/:id/thanks"
    {:swagger    {:tags ["answers"]}
     :middleware [[middleware/wrap-restricted]]
     :post       {:summary    "add."
                  :parameters {:path {:id integer?}}
                  :responses  {200 {:body {:code            int?
                                           :msg             string?
                                           (ds/opt :errors) any?
                                           (ds/opt :data)   any?}}}
                  :handler    (fn [{{body     :body
                                     {id :id} :path} :parameters
                                    token                        :identity}]
                                (ok (thanks-service/thanks token "answer" id)))}}]
   ["/answers/:id/vote"
    {:swagger    {:tags ["answers"]}
     :middleware [[middleware/wrap-restricted]]
     :post       {:summary    "add."
                  :parameters {:path {:id integer?}
                               :body {:vote_value integer?}}
                  :responses  {200 {:body {:code            int?
                                           :msg             string?
                                           (ds/opt :errors) any?
                                           (ds/opt :data)   any?}}}
                  :handler    (fn [{{body     :body
                                     {id :id} :path} :parameters
                                    token                        :identity}]
                                (ok (vote-service/vote token "answer" id body)))}}]
   ["/answers/:id/favorite"
    {:swagger    {:tags ["answers"]}
     :middleware [[middleware/wrap-restricted]]
     :post       {:summary    "add."
                  :parameters {:path {:id integer?}}
                  :responses  {200 {:body {:code            int?
                                           :msg             string?
                                           (ds/opt :errors) any?
                                           (ds/opt :data)   any?}}}
                  :handler    (fn [{{{id :id} :path} :parameters
                                    token            :identity}]
                                (ok (favorite-service/favorite token "answer" id)))}}]
   ["/answers/:id/comments"
    {:swagger {:tags ["answers"]}
     :post    {:summary    "add."
               :middleware [[middleware/wrap-restricted]]
               :parameters {:path {:id integer?}
                            :body {:message      string?
                                   (ds/opt :pid) integer?}}
               :responses  {200 {:body {:code            int?
                                        :msg             string?
                                        (ds/opt :errors) any?
                                        (ds/opt :data)   any?}}}
               :handler    (fn [{{{id :id} :path
                                  body     :body} :parameters
                                 token                        :identity}]
                             (ok (comment-service/create-model token "answer" id body)))}
     :get     {:summary    "get list."
               :parameters {:path {:id integer?}}
               :responses  {200 {:body {:code            int?
                                        :msg             string?
                                        (ds/opt :errors) any?
                                        (ds/opt :data)   any?}}}
               :handler    (fn [{{{id :id} :path} :parameters
                                 token            :identity}]
                             (ok (comment-service/get-models token "answer" id)))}}]
   ["/answers/:pid/comments/:id"
    {:swagger    {:tags ["answers"]}
     :middleware [[middleware/wrap-restricted]]
     :put        {:summary    "edit."
                  :middleware [[middleware/wrap-restricted]]
                  :parameters {:path {:id  integer?
                                      :pid integer?}
                               :body {:message string?}}
                  :responses  {200 {:body {:code            int?
                                           :msg             string?
                                           (ds/opt :errors) any?
                                           (ds/opt :data)   any?}}}
                  :handler    (fn [{{{id  :id
                                      pid :pid} :path
                                     body               :body} :parameters
                                    token                                  :identity}]
                                (ok (comment-service/edit-model token "answer" id body)))}
     :delete     {:summary    "remove."
                  :parameters {:path {:id  integer?
                                      :pid integer?}}
                  :responses  {200 {:body {:code            int?
                                           :msg             string?
                                           (ds/opt :errors) any?
                                           (ds/opt :data)   any?}}}
                  :handler    (fn [{{body               :body
                                     {id  :id
                                      pid :pid} :path} :parameters
                                    token                                  :identity}]
                                (ok (comment-service/remove-model token "answer" pid id)))}}]
   ["/drafts"
    {:swagger    {:tags ["draft"]}
     :middleware [[middleware/wrap-restricted]]
     :post       {:summary    "add."
                  :parameters {:body {:item_id integer?
                                      :type    string?
                                      :data    string?}}
                  :responses  {200 {:body {:code            int?
                                           :msg             string?
                                           (ds/opt :errors) any?
                                           (ds/opt :data)   any?}}}
                  :handler    (fn [{{body :body} :parameters
                                    token        :identity}]
                                (ok (draft-service/create-entity token body)))}
     :get        {:summary    "get list."
                  :parameters {:query {(ds/opt :type) string?}}
                  :responses  {200 {:body {:code            int?
                                           :msg             string?
                                           (ds/opt :errors) any?
                                           (ds/opt :data)   any?}}}
                  :handler    (fn [{{params :query} :parameters
                                    token           :identity}]
                                (ok (draft-service/get-entities token params)))}}]
   ["/drafts/:id"
    {:swagger    {:tags ["draft"]}
     :middleware [[middleware/wrap-restricted]]
     :put        {:summary    "edit."
                  :parameters {:path {:id integer?}
                               :body {:data string?}}
                  :responses  {200 {:body {:code            int?
                                           :msg             string?
                                           (ds/opt :errors) any?
                                           (ds/opt :data)   any?}}}
                  :handler    (fn [{{{id :id} :path
                                     body     :body} :parameters
                                    token                        :identity}]
                                (ok (draft-service/edit-entity token id body)))}
     :delete     {:summary    "remove."
                  :parameters {:path {:id integer?}}
                  :responses  {200 {:body {:code            int?
                                           :msg             string?
                                           (ds/opt :errors) any?
                                           (ds/opt :data)   any?}}}
                  :handler    (fn [{{body     :body
                                     {id :id} :path} :parameters
                                    token                        :identity}]
                                (ok (draft-service/remove-entity token id)))}}]
   ["/comments/:id/report"
    {:swagger    {:tags ["comments"]}
     :middleware [[middleware/wrap-restricted]]
     :post       {:summary    "add."
                  :parameters {:body {:reason string?}
                               :path {:id integer?}}
                  :responses  {200 {:body {:code            int?
                                           :msg             string?
                                           (ds/opt :errors) any?
                                           (ds/opt :data)   any?}}}
                  :handler    (fn [{{body     :body
                                     {id :id} :path} :parameters
                                    token                        :identity}]
                                (ok (report-service/report token "comments" id body)))}}]
   
   ["/comments/:id/like"
    {:swagger    {:tags ["comments"]}
     :middleware [[middleware/wrap-restricted]]
     :post       {:summary    "add."
                  :parameters {:path {:id integer?}}
                  :responses  {200 {:body {:code            int?
                                           :msg             string?
                                           (ds/opt :errors) any?
                                           (ds/opt :data)   any?}}}
                  :handler    (fn [{{body     :body
                                     {id :id} :path} :parameters
                                    token                        :identity}]
                                (ok (thanks-service/thanks token "comment" id)))}}]])
