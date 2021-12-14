(ns app.services.user.login
  (:require
   [honeysql.core :as hsql]
   [next.jdbc.sql :as sql]
   [next.jdbc.result-set :as rs]
   [java-time :as time]
   [clojure.tools.logging :as log]
   [app.db.core :refer [conn]]
   [app.config :refer [env]]
   [cuerdas.core :as str]
   [app.middleware.exception :as exception]
   [app.middleware :refer [secret]]
   [buddy.hashers :as hashers]
   [buddy.sign.jwt :as jwt]
   [chime.core :as chime]
   [app.services.sms :as sms-service]
   [app.services.token :as token]))

(defn enter-login [params]
  "enter login page check params"
  (let [entity (first (sql/find-by-keys conn :users params))]
    (if (empty? entity)
      (throw (ex-info "check" {:type ::exception/check :msg "user not exists!"})))

    (let [token (token/generate-token)]
      (sql/update! :users {:id (:id entity)} {:user_token         token
                                              :user_token_send_at (hsql/raw "now()")})
      {:code 0 :msg "success" :token token})))

(defn do-login [params headers addr]
  ""
  (let [entity (first (sql/find-by-keys conn :users (dissoc params :password :token :code)))]

    ;; check if user not exits
    (if (empty? entity)
      (throw (ex-info "check" {:type ::exception/check
                               :msg  "user not exists!"})))

    ;; check locked
    (if (:locked_at entity)
      (throw (ex-info "check" {:type ::exception/check
                               :msg  "locked!"})))

    ;; check token
    (if (not= (:token params) (:user_token entity))
      (throw (ex-info "check" {:type ::exception/check
                               :msg  "locked!"})))

    ;; check token time limit
    (let [send-time (time/minus (time/local-date-time) (time/minutes 5))]
      (if (time/before? (:user_token_send_at entity) send-time)
        (throw (ex-info "check" {:type ::exception/check
                                 :msg  "time limit!"}))))


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

          (throw (ex-info "check" {:type ::exception/check
                                   :msg  "password not match!"})))))


    ;; check code
    (sms-service/check-sms {:phone (:mobile user) :code (:code user)})

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
