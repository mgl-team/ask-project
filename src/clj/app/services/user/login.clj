(ns app.services.user.login
  (:require
   [honey.sql :as hsql]
   [next.jdbc.sql :as sql]
   [next.jdbc.result-set :as rs]
   [java-time :as time]
   [clojure.tools.logging :as log]
   [cuerdas.core :as str]
   [chime.core :as chime]
   [buddy.hashers :as hashers]
   [buddy.sign.jwt :as jwt]
   [app.db.core :as db :refer [conn]]
   [app.config :refer [env]]
   [app.services.sms :as sms-service]
   [app.services.check :as check-service]
   [app.services.token :as token]
   [app.middleware.exception :as exception]))

(defn enter-login [params]
  "enter login page check params"
  (let [entity (first (db/find-by-keys :users params))]

    ;; check user exist
    (check-service/check-must-exist entity "user does not exist!")

    (let [token (token/generate-token)
          sqlmap {:update :users, :set {:user_token         token
                                        :user_token_send_at [:raw "now()"]}
                  :where [:= :id (:id token)]}
          result (db/execute! (hsql/format sqlmap))]
      (log/warn "result = " result)

      {:code 0 :msg "success" :token token})))

(defn do-login [params headers addr]
  ""
  (let [entity (db/find-one-by-keys :users (dissoc params :password :token :code))]

    ;; check if user not exits
    (check-service/check-must-exist entity "user does not exist!")

    ;; check locked
    (check-service/check-locked entity "locked!")


    ;; check token
    ; (if (not= (:token params) (:user_token entity))
    ;   (exception/ex-throw "token not match!"))

    ;; check token time limit
    ; (let [send-time (time/minus (time/local-date-time) (time/minutes 5))]
    ;   (check-service/check-time-after (:user_token-send_at entity) send-time "time limit"))

    ;; check password
    (if (:password params)

      (if-not (hashers/check (:password params) (:encrypted_password entity))

        (let [attempt-params {:updated_at      [:raw "now()"]
                              :failed_attempts [:+ :failed_attempts 1]}
              attempts       (:failed_attempts entity)
              locked-params  (if attempts (> attempts 3)
                                 {:locked_at [:raw "now()"]})
              sqlmap {:update :users, :set (merge attempt-params locked-params)
                      :where [:= :id (:id entity)]}]

          (chime/chime-at [(.plusSeconds (time/instant) 1)]
                          (fn [time]
                            (let [result (db/execute! (hsql/format sqlmap))]
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
    (log/warn "update statistics start")
    (let [sqlmap {:update :users, :set {:sign_in_count      [:+ :sign_in_count 1]
                                        :current_sign_in_at [:raw "now()"]
                                        :last_sign_in_ip    [:raw "current_sign_in_ip"]
                                        :current_sign_in_ip addr
                                        :updated_at         [:raw "now()"]
                                        :failed_attempts    0}
                  :where [:= :id (:id entity)]}]
      ; (log/warn "sql = " (hsql/format sqlmap))
      (let [result (db/execute! (hsql/format sqlmap))]
        (log/info "update statistics record " result)))
      ; (chime/chime-at [(.plusSeconds (time/instant) 1)]
      ;                 (fn [time]
      ;                   (let [result (db/execute! (hsql/format sqlmap))]
      ;                     (log/warn "update statistics record " result)))))

    ;; generate token
    (let [token  (token/jwt-token (:id entity))]
      {:code  0
       :token token
       :msg   "success"})))


(defn login [user headers addr]
  (do-login user headers addr))
