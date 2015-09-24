(ns cetl.file.management
  (:require [clojure.string :as s])
  (:import (org.apache.commons.io FileUtils)
           (java.io File)))

(use '[clojure.java.shell :only [sh]])

;TODO add return values as in cetl-enccode funcs ie both return map x

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defmulti cetl-file-list (fn [x] (:list x)))

(defmethod cetl-file-list :dirs-and-files
  [path]
  (clojure.string/split
    (get (clojure.java.shell/sh
           "sh" "-c"
           (str " cd " (:path path) ";"
                " find `pwd` -maxdepth 1 "))
         :out) #"\n"))

(defmethod cetl-file-list :files
  [path]
  (clojure.string/split
    (get (clojure.java.shell/sh
           "sh" "-c"
           (str " cd " (:path path) ";"
                " find `pwd` -type f -maxdepth 1 "))
         :out) #"\n"))

(defmethod cetl-file-list :dirs
  [path]
  (clojure.string/split
    (get (clojure.java.shell/sh
           "sh" "-c"
           (str " cd " (:path path) ";"
                " find `pwd` -type d -maxdepth 1 "))
         :out) #"\n"))

(defmethod cetl-file-list :dirs-only-sub-dirs
  [path]
  (clojure.string/split
    (get (clojure.java.shell/sh
           "sh" "-c"
           (str " cd " {:path path} ";"
                " find `pwd` -type d "))
         :out) #"\n"))

(defmethod cetl-file-list :files-only-sub-dirs
  ;TODO implement
  []
  )

#_(defn cetl-file-unarchive
    [path in out & {:keys []}])

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;


(defmulti cetl-file-archive (fn [x] (:archive x)))

(defmethod cetl-file-archive :zip
  [path]
  (clojure.java.shell/sh
    "sh" "-c"
    (str " cd " (.getParent (File. (:path path))) "/;"
         " zip " (last (s/split (:path path) #"/")) ".zip"
         " -r " (last (s/split (:path path) #"/")))))

(defmethod cetl-file-archive :gzip
  [path]
  (clojure.java.shell/sh
    "sh" "-c"
    (str " cd " (.getParent (File. (:path path))) "/;"
         " tar -cvzf " (last (s/split (:path path) #"/")) ".tar.gz"
         " " (last (s/split (:path path) #"/")))))



;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defmulti cetl-file-encode (fn [x] (:encode x)))

(defmethod cetl-file-encode :UTF-8
  [x]
  (FileUtils/write
     (File. (:path x))
     (FileUtils/readFileToString
       (File.
         (:path x)) "ISO-8859-1")
     (name (:encode x))) x)

(defmethod cetl-file-encode :ISO-8859-15
  [x]
  (FileUtils/write
    (File. (:path x))
    (FileUtils/readFileToString
      (File.
        (:path x)) "UTF-8")
    (name (:encode x))) x)


;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;


















