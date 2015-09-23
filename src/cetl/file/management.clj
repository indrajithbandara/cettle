(ns cetl.file.management
  (:require [clojure.string :as s])
  (:import (org.apache.commons.io FileUtils)
           (java.io File)))

(use '[clojure.java.shell :only [sh]])


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

(defn cetl-change-file-encoding
  [in-path out-path & {:keys [from-encode to-encode]}]
  (letfn [(encoding
            [in out x xs]
            (FileUtils/write
              (File. out)
              (FileUtils/readFileToString
                (File. in) x) xs))]
    (cond (and (= from-encode :UTF-8)
               (= to-encode :ISO-8859-15))
          (encoding in-path out-path "UTF-8" "ISO-8859-15")
          (and (= from-encode :ISO-8859-15)
               (= to-encode :UTF-8))
          (encoding in-path out-path "ISO-8859-15" "UTF-8"))))


;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;























