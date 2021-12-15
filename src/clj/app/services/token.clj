(ns app.services.token
  (:require
    [buddy.hashers :as hashers]
    [buddy.core.codecs :as codecs]
    [buddy.core.hash :as hash]
    [buddy.sign.jwt :as jwt]
    [java-time :as time]
    [app.middleware :refer [secret]]))

(defn generate-token []
  (-> (hash/sha256 (.toString (java.util.UUID/randomUUID)))
      (codecs/bytes->hex)))

(defn jwt-token [entity]
  (let [uuid   (clojure.string/replace (.toString (java.util.UUID/randomUUID)) #"-" "")
        exp    (-> (time/plus (time/zoned-date-time) (time/days 90))
                   time/instant
                   time/to-millis-from-epoch)
        claims {:id  (:id entity)
                :jti uuid
                :exp exp}]
    (jwt/sign claims secret {:alg :hs512})))
