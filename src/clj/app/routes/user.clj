(ns app.routes.user
  (:require
   [ring.util.http-response :refer :all]
   [spec-tools.data-spec :as ds]
   [app.middleware :as middleware]
   [app.services.user.login :as login-service]
   [app.services.user.register :as register-service]
   [app.services.user.password :as password-service]
   [app.services.user.info :as info-service]))

(def route
  [["/user"
    {:swagger {:tags ["user"]}}
    ["/login"
     {:post {:summary "sign in."
             :operationId "sign-in"
             :parameters {:body {(ds/opt :username) string? (ds/opt :password) string?
                                 (ds/opt :code) string?
                                 (ds/opt :mobile) string?
                                 (ds/opt :email) string?}}
             :responses {200 {:body {:code int? :msg string?, (ds/opt :errors) any?
                                                            , (ds/opt :token) any?}}}
             :handler (fn [{{:keys [body]} :parameters headers :headers addr :remote-addr}]
                        {:status 200 :body
                         (login-service/login body headers addr)})}}]
    ;;
    ["/register"
     {:post {:summary "sign up."
             :operationId "sign-up"
             :parameters {:body {:code string?
                                 :mobile string?}}
             :responses {200 {:body {:code int? :msg string?, (ds/opt :errors) any?
                                                            , (ds/opt :token) any?}}}
             :handler (fn [{{:keys [body]} :parameters headers :headers addr :remote-addr}]
                        {:status 200 :body
                         (register-service/register body headers addr)})}}]]])
    ;;
