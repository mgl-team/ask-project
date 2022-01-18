(ns app.services.sms
  (:require
    [app.services.http-request :as http-service]
    [app.config :refer [env]]
    [clojure.tools.logging :as log]))

(defn- get-url [id]
  (str (:sms-url env) id))

(defn send-sms [params]
  (http-service/post (get-url "/api/send") params))

(defn check-sms [params]
  (http-service/post (get-url "/api/check") params))

(defn get-code [params]
  (http-service/get (str (get-url "/api/get/") (:mobile params)) {}))
