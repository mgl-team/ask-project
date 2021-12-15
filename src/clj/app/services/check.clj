(ns app.services.check
  (:require
   [app.middleware.exception :as exception]
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
