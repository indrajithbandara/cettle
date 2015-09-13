(ns cetl.file.management
  (:require [clojure.core.async :refer [chan <!! >!! >! <! put! take! close!
                                        sliding-buffer
                                        dropping-buffer
                                        thread go]]
            [clojure.string :as s]
            [cetl.utils.component-utils :as c-utils])
  (:import (java.io File FileFilter)))

(use '[clojure.java.shell :only [sh]])


;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;


;1. List files in directory
;2. Put each path onto a channel
;3. Pass each path to any input component
;TODO implement include-hidden-files? for files only
;TODO move zip command and gzip command into parent func

(defn cetl-file-list
  [path & {:keys
           [dirs-only? files-only? files-only? dirs-&-files?
            include-sub-dirs?]}]
  (letfn
    [(dirs-&-files
       [p]
       (clojure.string/split
         (get (clojure.java.shell/sh
                "sh" "-c"
                (str " cd " p ";" "  ls -d $PWD/*/ " ";" " ls -d -1 $PWD/*.* "))
              :out) #"\n"))
     (files-only
       [p]
       (clojure.string/split
         (get (clojure.java.shell/sh
                "sh" "-c"
                (str " cd " p ";" " ls -d -1 $PWD/*.* "))
              :out) #"\n"))
     (dirs-only
       [p]
       (clojure.string/split
         (get (clojure.java.shell/sh
                "sh" "-c"
                (str " cd " p ";" " ls -d -1 $PWD/*/ "))
              :out) #"\n"))
     (include-dirs-only-sub-dirs
       [p]
       (map #(str % "/")
         (clojure.string/split
           (get (clojure.java.shell/sh
                  "sh" "-c"
                  (str " cd " p ";" " find `pwd` -type d "))
                :out) #"\n")))
     (include-files-only-sub-dirs
       [p]
       (mapv #(.getPath %)
            (filter #(if (.isFile %) (.getPath %))
                    (file-seq (File. p)))))]
    (cond
      (and (= dirs-&-files? true)
           (= include-sub-dirs? false))
      (dirs-&-files path)
      (and (= files-only? true)
           (= include-sub-dirs? false))
      (files-only path)
      (and (= dirs-only? true)
           (= include-sub-dirs? false))
      (dirs-only path)
      (and (= dirs-only? true)
           (= include-sub-dirs? true))
      (include-dirs-only-sub-dirs path)
      (and (= files-only? true)
           (= include-sub-dirs? true))
      (include-files-only-sub-dirs path))))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defn zip-command
  [p]
  (str " cd " (c-utils/parent-folder p) ";"
       " zip " (c-utils/path-head p)".zip"
       " -r " (c-utils/path-head p)))

(defn gzip-command
  [p]
  (str " cd " (c-utils/parent-folder p) ";"
       " tar -cvzf " (c-utils/path-head p)".tar.gz"
       " " (c-utils/path-head p)))

(defn cetl-file-archive
  [path & {:keys [archive-format]}]
  (letfn
    [(zip
       [p]
       (clojure.java.shell/sh "sh" "-c" (zip-command p)))
     (gzip
       [p]
       (clojure.java.shell/sh "sh" "-c" (gzip-command p)))]
    (cond
      (= archive-format :zip) (zip path)
      (= archive-format :gzip) (gzip path))))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;



#_(defn cetl-file-unarchive
  [path in out & {:keys []}])
