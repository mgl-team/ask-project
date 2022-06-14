(ns app.routes.user
  (:require
   [ring.util.http-response :refer :all]
   [spec-tools.data-spec :as ds]
   [clojure.tools.logging :as log]
   [app.config :refer [env]]
   [app.middleware :as middleware]
   [app.services.user.login :as login-service]
   [app.services.user.register :as register-service]
   [app.services.user.password :as password-service]
   [app.services.user.info :as info-service]))

(def route
  [["/users/register"
    {:swagger {:tags ["users"]}
     :post    {:summary    "sign up."
               :parameters {:body {:code   string?
                                   :mobile string?}}
               :responses  {200 {:body {:code            int?
                                        :msg             string?
                                        (ds/opt :errors) any?
                                        (ds/opt :token)  any?}}}
               :handler    (fn [{{:keys [body]} :parameters
                                 headers        :headers
                                 addr           :remote-addr}]
                             {:status 200
                              :body   (register-service/register body headers addr)})}}]
   ["/users/login"
    {:swagger {:tags ["users"]}
     :post {:summary "sign in."
            :parameters {:body {(ds/opt :username) string? (ds/opt :password) string?
                                (ds/opt :code) string?
                                (ds/opt :mobile) string?
                                (ds/opt :email) string?}}
            :responses {200 {:body {:code int? :msg string?, (ds/opt :errors) any?
                                                           , (ds/opt :token) any?}}}
            :handler (fn [{{:keys [body]} :parameters headers :headers addr :remote-addr}]
                       {:status 200 :body
                        (login-service/login body headers addr)})}}]
   ["/users/check-mobile"
    {:swagger {:tags ["users"]}
     :post {:summary "check mobile."
            :parameters {:body {:mobile string?}}
            :responses {200 {:body {:code int? :msg string?, (ds/opt :errors) any?
                                                           , (ds/opt :token) any?}}}
            :handler (fn [{{:keys [body]} :parameters headers :headers addr :remote-addr}]
                       {:status 200 :body
                        (info-service/check-mobile body headers addr)})}}]

   ["/users/send-code"
    {:swagger {:tags ["users"]}
     :post {:summary "send code."
            :parameters {:body {:mobile string? :direction int?}}
            ; :responses {200 {:body {:code int? :msg string?, (ds/opt :errors) any?
            ;                                                , (ds/opt :token) any?}}}
            :handler (fn [{{:keys [body]} :parameters headers :headers addr :remote-addr}]
                       {:status 200 :body
                        (info-service/send-code body headers addr)})}}]
   (if (:dev env)
     ["/users/get-code"
      {:swagger {:tags ["users"]}
       :post {:summary "send code."
              :parameters {:body {:mobile string?}}
              :responses {200 {:body {:code int? :msg string?, (ds/opt :errors) any?
                                                             , (ds/opt :token) any?}}}
              :handler (fn [{{:keys [body]} :parameters headers :headers addr :remote-addr}]
                         {:status 200 :body
                          (info-service/get-code body)})}}])
   ["/users/info"
    {:swagger {:tags ["users"]}
     :middleware [middleware/wrap-restricted]
     :get {:summary "info ."
           :responses {200 {:body {:code int? :msg string?, (ds/opt :errors) any?
                                                          , (ds/opt :token) any?}}}
           :handler (fn [{{:keys [body]} :parameters headers :headers uinfo :identity}]
                      {:status 200 :body (info-service/user-info uinfo)})}}]
   ["/users/update-ex"
    {:swagger {:tags ["users"]}
     :post {:summary "update ex."
            :parameters {:body {(ds/opt :user_name) string?}}
            :handler (fn [{{body :body} :parameters uinfo :identity}]
                       {:status 200 :body
                        (info-service/update-ex uinfo body)})}}]])
