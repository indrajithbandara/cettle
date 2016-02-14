(ns cetl.shell.command.archive
  (:require [clojure.java.io :as io]
            [clojure.string :as s]
            [cetl.utils.file :refer [parent-path file-name]]
            [cetl.shell.sh :refer [exec-command exec-commands]]
            [cetl.utils.file :refer [file-name]]))

(use 'com.rpl.specter)


(defn compress-command
  ([s1]
   {:zip (str " zip " s1".zip -r " s1)
    :gzip (str " tar -cvzf " s1".tar.gz " s1)})
  ([s1 s2])
  ([s1 s2 s3]
   {:zip-exc-file (str " zip -r " s1".zip " s2 " -x " s2 s3)}))

(defn cetl-compress
  [cc]
  (fn [k x]
    (transform [ALL] (exec-command (k cc)) x)))