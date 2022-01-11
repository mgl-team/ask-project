(ns app.services.check
  (:require
   [app.middleware.exception :as exception]
   [app.db.core :as db]
   [buddy.hashers :as hashers]
   [java-time :as time]))

(defn check-must-exist [entity info]
  (if (empty? entity)
    (exception/ex-throw info)))

(defn check-must-not-exist [entity info]
  (if-not (empty? entity)
    (exception/ex-throw info)))

(defn check-locked [entity info]
  (if (:locked_at entity)
    (exception/ex-throw info)))

(defn check-time-after [t1 t2 info]
  (if (time/before? t1 t2)
    (exception/ex-throw info)))

(defn check-password-confirmation [p1 p2 info]
  (if-not (= p1 p2)
    (exception/ex-throw info)))

(defn check-current-password [p1 entity info]
  (if-not (hashers/check p1 (:encrypted_password entity))
    (exception/ex-throw info)))

;; ---
(defn check-http-status [http-response]
  (if (not= 200 (:status http-response))
    (throw (ex-info "service" {:type ::exception/check :msg "service unavilable! "}))))

(defn check-http-response [response]
  (if (= 1 (:code response))
    (throw (ex-info "service" {:type ::exception/check :msg (:msg response)}))))

(defn check-own-entity [uinfo entity msg]
  (if (not= (:id uinfo) (:user_id entity))
    (exception/ex-throw msg)))

(defn check-admin [uinfo msg]
  (let [entity (db/get-by-id :users (:id uinfo))]
    (check-must-exist entity "must exists")

    (if (not= 1 (:level entity))
      (exception/ex-throw msg))))
