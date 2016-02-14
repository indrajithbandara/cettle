(ns cetl.terminal
  (:require [clojure.string :as s]
            [clojure.java.io :as io]
            [clojure.set :refer [rename-keys]]
            [cetl.utils.file :refer [file-exists? file-name is-dir? parent-path check-path]]
            [cetl.shell.command.archive :refer [compress-command]]
            [cetl.shell.command.path :refer [path-command]]
            [cetl.shell.sh :refer [exec-command]]))

(use 'com.rpl.specter)


