(ns app.routes.services
  (:require
    [reitit.swagger :as swagger]
    [reitit.swagger-ui :as swagger-ui]
    [reitit.ring.coercion :as coercion]
    [reitit.coercion.spec :as spec-coercion]
    [reitit.ring.middleware.muuntaja :as muuntaja]
    [reitit.ring.middleware.multipart :as multipart]
    [reitit.ring.middleware.parameters :as parameters]
    [app.middleware.formats :as formats]
    [app.middleware :as middleware]
    [app.middleware.exception :as exception]
    [ring.util.http-response :refer :all]
    [clojure.java.io :as io]
    [spec-tools.data-spec :as ds]

    [app.services.user.login :as login-service]
    [app.routes.question :as question-route]
    [app.routes.answer :as answer-route]
    [app.routes.topic :as topic-route]))

(defn service-routes []
  (into []
        (concat
         ["/api"
          {:coercion spec-coercion/coercion
           :muuntaja formats/instance
           :swagger {:id ::api}
           :middleware [;; query-params & form-params
                        parameters/parameters-middleware
                        ;; content-negotiation
                        muuntaja/format-negotiate-middleware
                        ;; encoding response body
                        muuntaja/format-response-middleware
                        ;; exception handling
                        exception/exception-middleware
                        ; coercion/coerce-exceptions-middleware
                        ;; decoding request body
                        muuntaja/format-request-middleware
                        ;; coercing response bodys
                        coercion/coerce-response-middleware
                        ;; coercing request parameters
                        coercion/coerce-request-middleware
                        ;; multipart
                        multipart/multipart-middleware]}

          ;; swagger documentation
          ["" {:no-doc true
               :swagger {:info {:title "my-api"
                                :description "https://cljdoc.org/d/metosin/reitit"}}}

           ["/swagger.json"
            {:get (swagger/create-swagger-handler)}]

           ["/api-docs/*"
            {:get (swagger-ui/create-swagger-ui-handler
                    {:url "/api/swagger.json"
                     :config {:validator-url nil}})}]]

          ["/ping"
           {:get (constantly (ok {:message "pong"}))}]


          ["/user"
           {:swagger {:tags ["user"]}}
           ["/login"
            {:post {:summary "login."
                    :parameters {:body {(ds/opt :username) string? (ds/opt :password) string?
                                        (ds/opt :code) string? :token string?
                                        (ds/opt :mobile) string?
                                        (ds/opt :email) string?}}
                    :responses {200 {:body {:code int? :msg string?, (ds/opt :errors) any?
                                                                   , (ds/opt :token) any?}}}
                    :handler (fn [{{:keys [body]} :parameters headers :headers addr :remote-addr}]
                               {:status 200 :body
                                (login-service/login body headers addr)})}}]]]

         ;; other routes
         question-route/route
         answer-route/route
         topic-route/route)))


   ; ["/math"
   ;  {:swagger {:tags ["math"]}}
   ;
   ;  ["/plus"
   ;   {:get {:summary "plus with spec query parameters"
   ;          :parameters {:query {:x int?, :y int?}}
   ;          :responses {200 {:body {:total pos-int?}}}
   ;          :handler (fn [{{{:keys [x y]} :query} :parameters}]
   ;                     {:status 200
   ;                      :body {:total (+ x y)}})}
   ;    :post {:summary "plus with spec body parameters"
   ;           :parameters {:body {:x int?, :y int?}}
   ;           :responses {200 {:body {:total pos-int?}}}
   ;           :handler (fn [{{{:keys [x y]} :body} :parameters}]
   ;                      {:status 200
   ;                       :body {:total (+ x y)}})}}]]])

   ; ["/files"
   ;  {:swagger {:tags ["files"]}}
   ;
   ;  ["/upload"
   ;   {:post {:summary "upload a file"
   ;           :parameters {:multipart {:file multipart/temp-file-part}}
   ;           :responses {200 {:body {:name string?, :size int?}}}
   ;           :handler (fn [{{{:keys [file]} :multipart} :parameters}]
   ;                      {:status 200
   ;                       :body {:name (:filename file)
   ;                              :size (:size file)}})}}]
   ;
   ;  ["/download"
   ;   {:get {:summary "downloads a file"
   ;          :swagger {:produces ["image/png"]}
   ;          :handler (fn [_]
   ;                     {:status 200
   ;                      :headers {"Content-Type" "image/png"}
   ;                      :body (-> "public/img/warning_clojure.png"
   ;                                (io/resource)
   ;                                (io/input-stream))})}}]]])
