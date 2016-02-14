(ns cetl.shell.command.archive
  (:require [clojure.java.io :as io]
            [clojure.string :as s]
            [cetl.utils.file :refer [parent-path file-name]]
            [cetl.shell.sh :refer [exec-command exec-commands]]
            [cetl.utils.file :refer [file-name]]))

(use 'com.rpl.specter)

(defrecord CompressionFormat [zip gzip])

(defn compress-command
  [s]
  (CompressionFormat. (str " zip " s ".zip -r " s)
                      (str " tar -cvzf " s ".tar.gz " s)))

(defn cetl-compress
  [k f x]
  (transform [ALL] (exec-command (k (compress-command f))) x))