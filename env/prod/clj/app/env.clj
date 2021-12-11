(ns app.env
  (:require [clojure.tools.logging :as log]))

(def defaults
  {:init
   (fn []
     (log/info "\n-=[ask-project started successfully]=-"))
   :stop
   (fn []
     (log/info "\n-=[ask-project has shut down successfully]=-"))
   :middleware identity})
