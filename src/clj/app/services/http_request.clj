(ns app.services.http-request
  (:refer-clojure :exclude [get])
  (:require
    [clj-http.client :as client]
    [clojure.tools.logging :as log]
    [cheshire.core :as cheshire]
    [app.services.check :as check-service]
    [app.middleware.exception :as exception]))

(defn execute [method url params]
  (let [[http-fn params-key] (condp = method
                               :get [client/get :query-params]
                               :post [client/post :form-params])
        http-response        (http-fn url { :content-type :json
                                           :accept       :json
                                           params-key    params})]

    (log/warn "http-request started ........")

    ;; check http response status
    (check-service/check-http-status http-response)

    (let [json-message (cheshire/parse-string (:body http-response) true)]

      ;; check json response status
      (check-service/check-http-response json-message)

      (log/warn "http-request ended ........")

      ;; return json message
      json-message)))


(defn get [url params]
  (execute :get url params))

(defn post [url params]
  (execute :post url params))
