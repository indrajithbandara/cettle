(ns cetl.file.management
  (:require [clojure.string :as s]
            [clojure.java.io :as io])
  (:import (org.apache.commons.io FileUtils)
           (java.io File)
           (java.text SimpleDateFormat)))

(use '[clojure.java.shell :only [sh]])



;==============================================================================================

;TODO add output map to contain :value vector and alter funcs to take :file param as in zip and gzip

(defmulti cetl-file-list (fn [x] (:list x)))

(defmethod cetl-file-list :dirs-and-files
  [path]
  (let [move-to-dir " cd "
        command " find `pwd` -maxdepth 1 "
        next-command ";"]
    (clojure.set/rename-keys
      (assoc path :value
                  (s/split
                    (get (clojure.java.shell/sh
                           "sh" "-c"
                           (str move-to-dir (:path path)
                                next-command
                                command))
                         :out) #"\n"))
      {:list :exec})))

(defmethod cetl-file-list :files
  [path]
  (let [move-to-dir " cd "
        command  " find `pwd` -type f -maxdepth 1 "
        next-command ";"]
    (clojure.set/rename-keys
      (assoc path :value
                  (s/split
                    (get (clojure.java.shell/sh
                           "sh" "-c"
                           (str move-to-dir (:path path)
                                next-command
                                command))
                         :out) #"\n"))
      {:list :exec})))

(defmethod cetl-file-list :dirs
  [path]
  (let [move-to-dir " cd "
        command  " find `pwd` -type d -maxdepth 1 "
        next-command ";"]
    (clojure.set/rename-keys
      (assoc path :value
                  (s/split
                    (get (clojure.java.shell/sh
                           "sh" "-c"
                           (str move-to-dir (:path path)
                                next-command
                                command))
                         :out) #"\n"))
      {:list :exec})))

(defmethod cetl-file-list :dirs-only-sub-dirs
  [path]
  (let [move-to-dir " cd "
        command  " find `pwd` -type d "
        next-command ";"]
    (clojure.set/rename-keys
      (assoc path :value
                  (s/split
                    (get (clojure.java.shell/sh
                           "sh" "-c"
                           (str move-to-dir (:path path)
                                next-command
                                command))
                         :out) #"\n"))
      {:list :exec})))

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
        get-path (:path path)
        file-name (:file path)]
    (clojure.java.shell/sh
      "sh" "-c"
      (str move-to-dir (File. get-path) next-command
           zip-command (str file-name file-ext)
           rec-command file-name))
    (clojure.set/rename-keys
      (assoc path :value
                  (vector
                    (str (:path path) "/" (:file path) file-ext)))
      {:archive :exec})))

(defmethod cetl-file-archive :gzip
  [path]
  (let [move-to-dir " cd "
        gzip-command " tar -cvzf "
        next-command ";"
        file-ext ".tar.gz"
        get-path (:path path)
        file-name (:file path)]
    (clojure.java.shell/sh
      "sh" "-c"
      (str move-to-dir  (File. get-path) next-command
           gzip-command (str file-name file-ext" "file-name)))
    (clojure.set/rename-keys
      (assoc path :value
                  (vector (str (:path path) "/" (:file path) file-ext)))
      {:archive :exec})))

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
    (io/file
      (str (:path x) "/" (:file-name x)))))

;================================================================================================

(defn cetl-file-copy
  [x]
  (io/copy
    (io/file (:in-file-path x))
    (io/file (:out-file-path x))))

;================================================================================================

(defn cetl-file-delete
  [x]
  (io/delete-file
    (io/file
      (str (:path x) "/" (:file-name x)))))

;================================================================================================

;abs path of file (will need to get abs file pathe regardsless of of user dosent enter it)
;directory name
;base name
;read and write?
;file size
;modified-time (millis)
;modified str (java date)

(defn cetl-file-properties
  [x]
  (let [file (File. (:path x))
        abs-file-path (.getAbsolutePath file)
        parent-dir (.getParent file)
        file-name (.getName file)
        read-permissions (.canRead file)
        write-permissions (.canWrite file)
        execute-permissions (.canExecute file)
        file-size (->> (reduce #(/ %1 %2) [(.length file) 1048576])
                       double
                       (format "%.3f")
                       read-string)
        modified-time-millis (.lastModified file)
        modified-str (.format (SimpleDateFormat. "yyyy-MM-dd HH:mm:ss.SSS") modified-time-millis)]
    modified-str))

