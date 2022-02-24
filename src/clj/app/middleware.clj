(ns app.middleware
  (:require
    [app.env :refer [defaults]]
    [app.config :refer [env]]
    [ring-ttl-session.core :refer [ttl-memory-store]]
    [ring.middleware.defaults :refer [site-defaults wrap-defaults]]
    [buddy.auth.middleware :refer [wrap-authentication wrap-authorization]]
    [buddy.auth.accessrules :refer [restrict]]
    [buddy.auth :refer [authenticated?]]
    [buddy.auth.backends.session :refer [session-backend]]
    [buddy.auth.backends.token :refer [jwe-backend jws-backend]]
    [buddy.sign.jwt :refer [encrypt decrypt]]
    [buddy.core.nonce :refer [random-bytes]]
    [buddy.sign.util :refer [to-timestamp]]
    [buddy.core.codecs :refer [hex->bytes]]
    [ring.logger :as logger]
    app.middleware.exception)
  (:import
   [java.util Calendar Date]))


(defn on-error [request response]
  {:status 403
   :headers {}
   :body (str "Access to " (:uri request) " is not authorized")})

(defn wrap-restricted [handler]
  (restrict handler {:handler authenticated?
                     :on-error on-error}))

(def secret
  ; (random-bytes 32)
  (hex->bytes
    "31af673fa9cc365da7ec44f1575f7d0916ef71b08232716c9488a3da2ec889b6"))
; buddy.core.codecs/bytes->hex

(def token-backend
  (jwe-backend {:secret secret
                ; :token-name "Barear"
                :options {:alg :a256kw
                          :enc :a128gcm}}))


(defn token [username]
  (let [claims {:user (keyword username)
                :exp (to-timestamp
                       (.getTime
                         (doto (Calendar/getInstance)
                           (.setTime (Date.))
                           (.add Calendar/HOUR_OF_DAY 1))))}]
    (encrypt claims secret {:alg :a256kw :enc :a128gcm})))

(defn dec-data [data]
  (decrypt data secret {:alg :a256kw :enc :a128gcm}))

(defn wrap-auth [handler]
  (let [backend token-backend]
    (-> handler
        (wrap-authentication backend)
        (wrap-authorization backend))))

(defn wrap-base [handler]
  (-> ((:middleware defaults) handler)
      logger/wrap-log-response
      wrap-auth
      (wrap-defaults
        (-> site-defaults
            (assoc-in [:security :anti-forgery] false)))
      logger/wrap-log-request-start))
