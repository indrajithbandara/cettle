(ns cetl.shell.command.path
  (:require [clojure.string :as s]
            [cetl.shell.sh :refer [exec-command]]))

(use 'com.rpl.specter)

(defrecord FilePath [file dir file-dir dir-subdir])

(def path-command
  (FilePath. " find `pwd ` -type f -maxdepth 1 "
             " find `pwd` -type d -not -path '*/\\.*' -maxdepth 1 "
             " find `pwd` -maxdepth 1 "
             " find `pwd` -type d "))

(defn cetl-list
  [k x]
  (transform [ALL] (exec-command (k path-command)) x))



