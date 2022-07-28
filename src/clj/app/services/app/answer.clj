(ns app.services.app.answer
  (:require
   [honey.sql :as hsql]
   [java-time :as time]
   [next.jdbc :as jdbc]
   [next.jdbc.sql :as sql]
   [next.jdbc.result-set :as rs]
   [clojure.tools.logging :as log]
   [cuerdas.core :as str]
   [cheshire.core :as cheshire]
   [app.db.core :as db :refer [conn]]
   [app.config :refer [env]]
   [app.services.check :as check-service]
   [app.middleware.exception :as exception]
   [ring.util.http-response :refer [ok bad-request]]))

(declare fix-content)

(defn get-models [uinfo pid]
  (log/info " uinfo = " uinfo)
  (log/info " pid = " pid)
  (if (:id uinfo)
    (let [sqlmap {:select [:a.*
                           [[:case [:not= :b.id nil] 1 :else 0] "user_thanks"]
                           [[:case [:= :c.vote_value 1] 1 [:= :c.vote_value nil] 0 :else 0] "user_vote_up"]
                           [[:case [:= :c.vote_value 1] 0 [:= :c.vote_value nil] 0 :else 1] "user_vote_down"]
                           [[:case [:not= :d.id nil] 1 :else 0] "user_favorite"]]
                  :from [[:v_answer :a]]
                  :left-join [[:thanks :b] [:and
                                            [:= :a.id :b.item_id]
                                            [:= :b.type "answer"]
                                            [:= :b.user_id (:id uinfo)]]
                              [:vote :c] [:and
                                          [:= :a.id :c.item_id]
                                          [:= :c.type "answer"]
                                          [:= :c.user_id (:id uinfo)]]
                              [:favorite :d] [:and
                                              [:= :a.id :d.item_id]
                                              [:= :d.type "answer"]
                                              [:= :d.user_id (:id uinfo)]]]
                  :where [:= :question_id pid]
                  :order-by [[:id :desc]]}
          data (->> (db/execute! (hsql/format sqlmap))
                    (fix-content))]
      {:code 0
       :msg "success"
       :data data})

    (let [data (->> (db/find-by-keys :v_answer  {:question_id pid}
                     {:order-by [[:id :desc]]})
                    (fix-content))]
      {:code 0
       :msg "success"
       :data data})))

(defn get-model [uinfo id]
  (let [data (db/get-by-id :answer id {:columns [:content :id]})]
    {:code 0
     :msg "success"
     :data data}))

(defn create-model [uinfo pid params]
  (log/info "uinfo = " uinfo " pid = " pid)
  (log/info "params = " params)
  (jdbc/with-transaction [tx conn]
    (let [user-answer (sql/find-by-keys tx :answer {:user_id      (:id uinfo)
                                                    :question_id  pid})]
      (if (empty? user-answer)
        (do
          (sql/insert! tx :answer
                       (assoc params :question_id pid
                              :user_id (:id uinfo)))

          (let [entity      (sql/get-by-id tx :question pid
                                           {:builder-fn rs/as-unqualified-lower-maps})
                user-answer (sql/find-by-keys tx :answer {:user_id     (:id uinfo)
                                                          :question_id pid})]
            (sql/update! tx :question
                         (merge
                          { :updated_at   (time/local-date-time)
                           :answer_count (inc (:answer_count entity))}
                          (if (empty? user-answer)
                            { :answer_user (inc (:answer_user entity))}))
                         {:id pid}))
          (ok {:code 0
               :msg  "success"}))
        (bad-request {:code 1
                      :msg "errors/already-has-answer"})))))

(defn edit-model [uinfo pid id params]
  (log/info "uinfo = " uinfo)
  (log/info "params = " params)
  (let [model (db/get-by-id :answer id)]
    (check-service/check-must-exist model "must exists")

    (check-service/check-own-entity uinfo model "must own entity")

    (db/update! :answer params {:id id}))
  {:code 0
   :msg  "success"})

(defn remove-model [uinfo id]
  (let [model (db/get-by-id :answer id)]
    (check-service/check-must-exist model "must exists")

    (check-service/check-own-entity uinfo model "must own entity")

    (log/warn "model = " (select-keys model [:content]))
    (jdbc/with-transaction [tx conn]
      (sql/delete! tx :answer {:id id})

      (let [json-value      (cheshire/generate-string (select-keys model [:content]))
            sqlmap-approval {:insert-into :approval
                             :values      [{:item_id id
                                            :type    "answer"
                                            :user_id (:id uinfo)
                                            :data    json-value}]}

            result          (jdbc/execute-one! tx (hsql/format sqlmap-approval)
                                               {:return-keys true
                                                :builder-fn  rs/as-unqualified-lower-maps})

            sqlmap          {:insert-into :approval_log,
                             :values      [{:status          1
                                            :approve_id      (:id result)
                                            :data            json-value
                                            :approve_user_id 0}]}]

        (jdbc/execute-one! tx (hsql/format sqlmap)))

      (let [sqlmap {:update :question
                    :set    {:answer_count [:- :answer_count 1]}
                    :where  [:= :id (:question_id model)]}]
        (jdbc/execute-one! tx (hsql/format sqlmap)))))
  {:code 0
   :msg  "success"})

(defn- fix-content [data]
  ;; delete last word, because of last word may trancated
  ;; the database return size is 160
  (->> data
      (map
        (fn [x]
          (if (< (count (:content x)) 160)
            x
            (let [content (:content x)
                  last-index (clojure.string/last-index-of content " ")]
              (assoc x :content (subs content 0 last-index))))))))
