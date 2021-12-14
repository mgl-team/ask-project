(ns app.services.token
  (:require
    [buddy.hashers :as hashers]
    [buddy.core.codecs :as codecs]
    [buddy.core.hash :as hash]
    [buddy.sign.jwt :as jwt]))

(defn generate-token []
  (-> (hash/sha256 (.toString (java.util.UUID/randomUUID)))
      (codecs/bytes->hex)))
