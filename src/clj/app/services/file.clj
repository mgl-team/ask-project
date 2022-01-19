(ns app.services.file
  (:require
   [next.jdbc.sql :as sql]
   [next.jdbc.result-set :as rs]
   [app.db.core :as db]
   [clojure.java.io :as io]
   [clojure.string :as str]
   [clojure.tools.logging :as log]
   [java-time :as time]
   [app.middleware.exception :as exception]
   [app.config :refer [env]]
   [ring.util.http-response :refer [content-type header ok file-response]]))

(defn add-file [file is-avatar uinfo]
  (let [path     "public/img"
        time-now (str/split (time/format "yyyy-MM" (time/local-date)) #"-")
        uuid     (str (str/replace (java.util.UUID/randomUUID) #"-" "") "." (-> file :filename (str/split #"\.") last))
        file-url (apply str (interpose "/" (concat [(str (:base-url env) "/api/file") (str (:id uinfo))] time-now [uuid])))
        filename (apply str (interpose "/" (concat [path (str (:id uinfo))] time-now [uuid])))]

    (io/make-parents filename)
    (io/copy (:tempfile file) (io/file filename))

    (if is-avatar
      (db/update! :user_ex {:avatar_file file-url} {:id (:id uinfo)})
      (let [params {:filename (:filename file)
                    :url file-url}
            result (db/insert! :attach_file params)]
        (ok {:success true
             :msg     "success"
             :data    (select-keys result [:id :url :filename])})))))

(defn remove-file [id]
  (let [result (db/get-by-id :attach_file id)]
    (if (empty? result)
      (throw (ex-info "check" {:type ::exception/check
                               :msg  "no file!"})))

    (let [path     "public/img/"
          filename (->> (subs (:url result) 10)
                        (str path))]
      (io/delete-file filename))

    (let [result (db/delete! :attach_file {:id id})]
      (log/warn "delete result " result))

    (ok {:success true
         :msg     "success"})))



(defn get-files [params]
  (let [result (db/find-by-keys :attach_file params)]
    (ok {:success true
         :msg     "success"
         :data    result})))
