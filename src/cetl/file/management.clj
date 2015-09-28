(ns cetl.file.management
  (:require [clojure.string :as s]
            [clojure.java.io :as io])
  (:import (org.apache.commons.io FileUtils)
           (java.io File)))

(use '[clojure.java.shell :only [sh]])

;TODO add return vals for files, dirs and dirs-only-sub-dirs

;==============================================================================================

(defmulti cetl-file-list (fn [x] (:list x)))

(defmethod cetl-file-list :dirs-and-files
  [path]
  (let [move-to-dir " cd "
        command " find `pwd` -maxdepth 1 "
        next-command ";"]
    (clojure.set/rename-keys
      (assoc path :value
                  (clojure.string/split
                    (get (clojure.java.shell/sh
                           "sh" "-c"
                           (str move-to-dir (:path path)
                                next-command
                                command))
                         :out) #"\n"))
      {:list :exec})))

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
           (str " cd " (:path path) ";"
                " find `pwd` -type d "))
         :out) #"\n"))

(defmethod cetl-file-list :files-only-sub-dirs
  ;TODO implement
  []
  )

#_(defn cetl-file-unarchive
    [path in out & {:keys []}])

;=================================================================================================


(defmulti cetl-file-archive (fn [x] (:archive x)))

(defmethod cetl-file-archive :zip
  [path]
  (let [move-to-dir " cd "
        zip-command " zip "
        rec-command " -r "
        next-command ";"
        file-ext ".zip"
        get-path (:path path)]
    (clojure.java.shell/sh
      "sh" "-c"
      (str move-to-dir (.getParent (File. get-path)) next-command
           zip-command (-> (s/split get-path #"/") last) file-ext
           rec-command (-> (s/split get-path #"/") last)))
    (assoc path :path (str (:path path) file-ext))))

(defmethod cetl-file-archive :gzip
  [path]
  (let [move-to-dir " cd "
        gzip-command " tar -cvzf "
        next-command ";"
        file-ext ".tar.gz "
        get-path (:path path)]
    (clojure.java.shell/sh
      "sh" "-c"
      (str move-to-dir (.getParent (File. get-path)) next-command
           gzip-command (-> (s/split get-path #"/") last) file-ext
           (-> (s/split get-path #"/") last)))
    (assoc path :path (str (:path path) file-ext))))

;=================================================================================================


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


;=================================================================================================


(defn cetl-file-temp-create
  [x]
  (io/writer
    (File.
      (str (:path x) "/" (:file-name x)))))

;================================================================================================

(defn cetl-file-copy
  [x]
  (io/copy
    (io/file (:in-file-path x))
    (io/file (:out-file-path x))))

;================================================================================================























