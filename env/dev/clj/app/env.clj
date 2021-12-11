(ns app.env
  (:require
    [clojure.tools.logging :as log]
    [app.dev-middleware :refer [wrap-dev]]))

(def defaults
  {:init
   (fn []
     (log/info "\n-=[ask-project started successfully using the development profile]=-"))
   :stop
   (fn []
     (log/info "\n-=[ask-project has shut down successfully]=-"))
   :middleware wrap-dev})
