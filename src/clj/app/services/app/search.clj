(ns app.services.app.search
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
   [app.services.jdbc-pager :as pager]
   [app.services.http-request :as http]
   [clj-http.client :as client]
   [app.middleware.exception :as exception]))

(defn is-local-engine [] (= "local" (get-in env [:search-engine :type])))

(defn get-url [u] (str (get-in env [:search-engine :base-url]) u))

(defn search-questions [uinfo params]
  (log/info " search-content = " params)
  ;; default search engine Postgresql full text search
  (if (is-local-engine)
    (let [query-key (str "%" (:search params) "%")
          ;
          page-params (select-keys params [:page :perpage])
          ;
          sqlmap {:select [:a.*
                           [[:case [:not= :b.id nil] 1 :else 0] "user_focus"]]
                  :from [[:v_question :a]]
                  :left-join [[:focus :b] [:and
                                           [:= :a.id :b.item_id]
                                           [:= :b.type "question"]
                                           [:= :b.user_id (:id uinfo)]]]
                  :where [:like :a.question_content query-key]
                  :order-by [[:a.id :desc]]}
          data (pager/query-with-join sqlmap page-params)]
      {:code 0
       :msg "success"
       :data data})
    (let [search        (:search params)
          params        (assoc-in
                          (get-in env [:search-engine :question :params])
                          [:query :multi_match :query]
                          search)
          response      (http/post
                          (get-url (get-in env [:search-engine :question :url]))
                          params)
          data          {:total   (get-in response [:hits :total :value])
                         :result  (map (fn [i] (merge {:id (:_id i)} (:_source i)
                                                      (if (:highlight i)
                                                        (:highlight i))))
                                    (get-in response [:hits :hits]))}]
      {:code 0
       :msg "success"
       :data data})))


(defn search-questions-you-type [uinfo params]
  (log/info " search-as-you-type = " params)
  (let [search (:search params)
        params        (assoc-in
                       (get-in env [:search-engine :question :search-as-params])
                       [:query :multi_match :query]
                       search)
        response     (http/post
                      (get-url (get-in env [:search-engine :question :url]))
                      params)
        data          {:total   (get-in response [:hits :total :value])
                       :result  (map (fn [i] (merge {:id (:_id i)} (:_source i)
                                                    (if (:highlight i)
                                                      (:highlight i))))
                                     (get-in response [:hits :hits]))}]
    {:code 0
     :msg "success"
     :data data}))

(defn search-question-answers [uinfo params]
  (log/info " search-question-answer = " params)
  (let [search (:search params)
        params        (assoc-in
                        (get-in env [:search-engine :question-answer :params])
                        [:query :multi_match :query]
                        search)
        response      (http/post
                        (get-url (get-in env [:search-engine :question-answer :url]))
                        params)
        data          {:total   (get-in response [:hits :total :value])
                       :result  (map (fn [i] (merge {:id (:_id i)} (:_source i)
                                                    (if (:highlight i)
                                                      (:highlight i))))
                                  (get-in response [:hits :hits]))}]
    {:code 0
     :msg "success"
     :data data}))

(defn search-articles [uinfo params]
  (log/info " search-content = " params)
  (let [search (:search params)
        data (if (is-local-engine)
               []
               (http/get
                 (get-url (get-in env [:search-engine :article :url]))
                 ((get-in env [:search-engine :article :params]) search)))]
    {:code 0
     :msg "success"
     :data data}))

(defn search-articles-you-type [uinfo params]
  (log/info " search-as-you-type = " params)
  (let [search (:search params)
        data (if (is-local-engine)
               []
               (http/get
                 (get-url (get-in env [:search-engine :article :url]))
                 ((get-in env [:search-engine :article :you-type-params]) search)))]
    {:code 0
     :msg "success"
     :data data}))

(comment
  (search-questions {} {:search "ᠰᠣᠨᠢᠨ"})
  (search-question-answers {} {:search "hell"})
  (search-questions-you-type {} {:search "ᠶᠠ"})
  )