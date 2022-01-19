(ns app.routes.file
  (:require
   [app.middleware :as middleware]
   [spec-tools.data-spec :as ds]
   [ring.util.http-response :refer [content-type header ok file-response]]
   [reitit.ring.middleware.multipart :as multipart]
   [app.services.file :as file-service]))


(def route
  [["/file/:id/:year/:month/:filename"
    {:get {:summary    "get a file"
           :swagger    {:tags ["files"]}
           :parameters {:path {:filename string?
                               :year     string?
                               :month    string?
                               :id      string?}}
           :handler    (fn [{{{filename :filename
                               year     :year
                               month    :month
                               id      :id} :path} :parameters
                             token                   :identity}]
                         (file-response (str "/img/" id "/" year "/" month "/" filename) {:root "public"}))}}]
   ["/file/upload"
    {:post {:summary    "put a file"
            :swagger    {:tags ["files"]}
            :parameters {:multipart {:file         multipart/temp-file-part}}
            :responses  {200 {:body {:success       boolean?
                                     :msg           string?
                                     (ds/opt :data) any?}}}
            :handler    (fn [{{{:keys [file accession_id test_type_id]} :multipart} :parameters
                              token :identity}]
                          (let [params {}]
                            (file-service/add-file file false token)))}}]
   ["/file/upload-avatar"
    {:post {:summary    "put a avatar"
            :middleware [[middleware/wrap-restricted]]
            :swagger    {:tags ["files"]}
            :parameters {:multipart {:file         multipart/temp-file-part}}
            :responses  {200 {:body {:success       boolean?
                                     :msg           string?
                                     (ds/opt :data) any?}}}
            :handler    (fn [{{{:keys [file accession_id test_type_id]} :multipart} :parameters
                              token :identity}]
                          (let [params {}]
                            (file-service/add-file file true token)))}}]])
