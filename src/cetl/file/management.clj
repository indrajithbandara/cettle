(ns cetl.file.management
  (:require [clojure.string :as s]
            [clojure.java.io :as io]
            [clojure.set :refer [rename-keys]])
  (:import (org.apache.commons.io FileUtils)
           (java.io File)
           (java.text SimpleDateFormat)))

(use '[clojure.java.shell :only [sh]])



;==============================================================================================

(defmulti cetl-file-list (fn [x] (:list x)))

(defmethod cetl-file-list :dirs-and-files
  [path]
  (let [move-to-dir " cd "
        command " find `pwd` -maxdepth 1 "
        next-command ";"]
    (rename-keys
      (assoc path :result
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
    (rename-keys
      (assoc path :result
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
    (rename-keys
      (assoc path :result
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
    (rename-keys
      (assoc path :result
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
  [x]
  (let [move-to-dir " cd "
        zip-command " zip "
        rec-command " -r "
        next-command ";"
        file-ext ".zip"
        path (:path x)
        file (:file x)]
    (clojure.java.shell/sh
      "sh" "-c"
      (str move-to-dir (File. path) next-command
           zip-command (str file file-ext)
           rec-command file))
    (rename-keys
      (assoc x :result
                  (vector
                    (str (:path x) "/" (:file x) file-ext)))
      {:archive :exec})))

(defmethod cetl-file-archive :gzip
  [x]
  (let [move-to-dir " cd "
        gzip-command " tar -cvzf "
        next-command ";"
        file-ext ".tar.gz"
        path (:path x)
        file (:file x)]
    (clojure.java.shell/sh
      "sh" "-c"
      (str move-to-dir  (File. path) next-command
           gzip-command (str file file-ext" "file)))
    (rename-keys
      (assoc x :result
                  (vector (str (:path x) "/" (:file x) file-ext)))
      {:archive :exec})))

;=================================================================================================


(defmulti cetl-file-encode (fn [x] (:encode x)))

(defmethod cetl-file-encode :ISO-8859-1
  [x]
  (let [file (:file x)
        path (:path x)
        file-path (str path "/" file)]
    (FileUtils/write
      (File. file-path)
      (FileUtils/readFileToString
        (File. file-path) "UTF-8")
      (name (:encode x))))
  (rename-keys
    (assoc x :result (vector (str (:path x) "/" (:file x))))
    {:encode :exec}))

(defmethod cetl-file-encode :UTF-8
  [x]
  (let [file (:file x)
        path (:path x)
        file-path (str path "/" file)]
    (FileUtils/write
      (File. file-path)
      (FileUtils/readFileToString
        (File. file-path) "ISO-8859-1")
      (name (:encode x))))
  (rename-keys
    (assoc x :result (vector (str (:path x) "/" (:file x))))
    {:encode :exec}))


;=================================================================================================


(defn cetl-file-temp-create
  [x]
  (let [file (:file x)
        path (:path x)
        exec (:create x)]
    (if (= exec :temp-file)
      (io/writer
        (io/file
          (str path "/" file))))
    (rename-keys
      (assoc x :result (vector (str (:path x) "/" (:file x))))
      {:create :exec})))

;================================================================================================

(defn cetl-file-copy
  [x]
  (let [file (:file x)
        in-path (:in-path x)
        out-path (:out-path x)
        exec (:copy x)]
    (if (= exec :file-copy)
      (io/copy
        (io/file (str in-path "/" file))
        (io/file (str out-path "/"file))))
    (rename-keys
      (assoc x :result (vector (str (:in-path x) "/" (:file x))
                               (str (:out-path x) "/" (:file x))))
      {:copy :exec})))

;================================================================================================

(defn cetl-file-delete
  [x]
  (let [file (:file x)
        path (:path x)
        exec (:delete x)]
    (if (= exec :file-delete)
      (io/delete-file
        (io/file
          (str path "/" file))))
    (rename-keys
      (assoc x :result (vector (str (:path x) "/" (:file x))))
      {:delete :exec})))

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