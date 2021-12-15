(ns app.services.user.login
  (:require
   [honeysql.core :as hsql]
   [next.jdbc.sql :as sql]
   [next.jdbc.result-set :as rs]
   [java-time :as time]
   [clojure.tools.logging :as log]
   [cuerdas.core :as str]
   [chime.core :as chime]
   [buddy.hashers :as hashers]
   [buddy.sign.jwt :as jwt]
   [app.db.core :refer [conn]]
   [app.config :refer [env]]
   [app.services.sms :as sms-service]
   [app.services.check :as check-service]
   [app.services.token :as token]
   [app.middleware.exception :as exception]
   [app.middleware :refer [secret]]))

(defn enter-login [params]
  "enter login page check params"
  (let [entity (first (sql/find-by-keys conn :users params
                        {:builder-fn rs/as-unqualified-lower-maps}))]

    ;; check user exist
    (check-service/check-must-exist entity "user does not exist!")

    (let [token (token/generate-token)]
      (sql/update! :users {:id (:id entity)} {:user_token         token
                                              :user_token_send_at (hsql/raw "now()")})
      {:code 0 :msg "success" :token token})))

(defn do-login [params headers addr]
  ""
  (let [entity (first (sql/find-by-keys conn :users (dissoc params :password :token :code)
                        {:builder-fn rs/as-unqualified-lower-maps}))]

    ;; check if user not exits
    (check-service/check-must-exist entity "user does not exist!")

    ;; check locked
    (check-service/check-locked entity "locked!")


    ;; check token
    (if (not= (:token params) (:user_token entity))
      (throw (ex-info "check" {:type ::exception/check
                               :msg  "token not matched"})))

    ;; check token time limit
    (let [send-time (time/minus (time/local-date-time) (time/minutes 5))]
      (check-service/check-time-after (:user_token-send_at entity) send-time "time limit"))

    ;; check password
    (if (:password params)

      (if-not (hashers/check (:password params) (:encrypted_password entity))

        (let [attempt-params {:updated_at      (hsql/raw "now()")
                              :failed_attempts (hsql/call :+ :failed_attempts 1)}
              attempts       (:failed_attempts entity)
              locked-params  (if attempts (> attempts 3)
                                 {:locked_at (hsql/raw "now()")})]

          (chime/chime-at [(.plusSeconds (time/instant) 1)]
                          (fn [time]
                            (let [result (sql/update! conn :users {:id (:id entity)} (merge attempt-params locked-params))]
                              (log/info "login attempts update result " result))))

          (exception/ex-throw "password not match!"))))


    ;; check code
    (if (:code params)
      (sms-service/check-sms {:phone (:mobile params) :code (:code params)}))

    ;; check no password and no code
    (if (and (empty? (:code params)) (empty? (:password params)))
      (exception/ex-throw "password and code!"))

    ;; check ended ..................

    ;; update statistics record
    (let [up-params {:sign_in_count      (hsql/call :+ :sign_in_count 1)
                     :current_sign_in_at (hsql/raw "now()")
                     :last_sign_in_ip    (hsql/call := :current_sign_in_ip)
                     :current_sign_in_ip addr
                     :updated_at         (hsql/raw "now()")
                     :failed_attempts    0}]
      (chime/chime-at [(.plusSeconds (time/instant) 1)]
                      (fn [time]
                        (let [result (sql/update! conn :users {:id (:id entity)} up-params)]
                          (log/info "update statistics record " result)))))

    ;; generate token
    (let [uuid   (clojure.string/replace (.toString (java.util.UUID/randomUUID)) #"-" "")
          exp    (-> (time/plus (time/zoned-date-time) (time/days 90))
                     time/instant
                     time/to-millis-from-epoch)
          claims {:id  (:id entity)
                  :jti uuid
                  :exp exp}
          token  (jwt/sign claims secret {:alg :hs512})]


      {:code  0
       :token token
       :msg   "success"})))


(defn login [user headers addr]
  (do-login user headers addr))
