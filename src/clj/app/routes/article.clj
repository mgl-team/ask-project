(ns app.routes.article
  (:require
   [app.middleware :as middleware]
   [ring.util.http-response :refer :all]
   [spec-tools.data-spec :as ds]
   [app.services.app.article :as service]))

(def route
  [["/articles"
    {:swagger {:tags ["articles"]}
     ; :middleware [[middleware/wrap-restricted]]
     :post    {:summary    "add."
               :parameters {:body {:title string?
                                   :content string?
                                   (ds/opt :source) string?}}
               :responses  {200 {:body {:success       boolean?
                                        :msg           string?
                                        (ds/opt :data) any?}}}
               :handler    (fn [{{body :body}       :parameters
                                 token                        :identity}]
                             (ok (service/create-model token body)))}
     :get     {:summary    "get list."
               :parameters {:query {(ds/opt :page)    int?
                                    (ds/opt :perpage) int?}}
               :responses  {200 {:body {:success       boolean?
                                        :msg           string?
                                        (ds/opt :data) any?}}}
               :handler    (fn [{{:keys [query]}    :parameters
                                 token                        :identity}]
                             (ok (service/get-models token query)))}}]
   ["/articles/:id"
    {:swagger {:tags ["articles"]}
     ; :middleware [[middleware/wrap-restricted]]
     :put     {:summary    "edit."
               :parameters {:path {:id integer?}
                            :body {(ds/opt :title) string?
                                   (ds/opt :content) string?
                                   (ds/opt :source) string?}}
               :responses  {200 {:body {:success       boolean?
                                        :msg           string?
                                        (ds/opt :data) any?}}}
               :handler    (fn [{{body     :body
                                  {id :id} :path} :parameters
                                 token                        :identity}]
                             (ok (service/edit-model token body)))}

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
               :parameters {:path {:id integer?}}
               :responses  {200 {:body {:success       boolean?
                                        :msg           string?
                                        (ds/opt :data) any?}}}
               :handler    (fn [{{{id :id} :path}   :parameters
                                 token                        :identity}]
                             (ok (service/get-model token id)))}}]])
