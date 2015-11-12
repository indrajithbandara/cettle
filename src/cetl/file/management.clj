(ns cetl.file.management
  (:require [clojure.string :as s]
            [clojure.java.io :as io]
            [clojure.set :refer [rename-keys]]
            [cetl.utils.component-utils :refer [file-exists? dir-exists?]])
  (:import (org.apache.commons.io FileUtils)
           (java.io File)
           (java.text SimpleDateFormat)))

(use '[clojure.java.shell :only [sh]])

;TODO add error handling for delete-file and file-properties and encode funcs
;Change multi methods to defrecords

;==============================================================================================

(defmulti cetl-list-file (fn [x] (:exec x)))

(defmethod cetl-list-file :list-dirs-files
  [path]
  (let [move-to-dir " cd "
        command " find `pwd` -maxdepth 1 "
        next-command ";"]
    (if (dir-exists? (:path path))
      (assoc path :result
                  (s/split
                    (get (clojure.java.shell/sh
                           "sh" "-c"
                           (str move-to-dir (:path path)
                                next-command
                                command))
                         :out) #"\n"))
      (throw (IllegalArgumentException.
               (str (:path path) " is not a directory"))))))


(defmethod cetl-list-file :list-files
  [path]
  (let [move-to-dir " cd "
        command  " find `pwd` -type f -maxdepth 1 "
        next-command ";"]
    (if (dir-exists? (:path path))
      (assoc path :result
                  (s/split
                    (get (clojure.java.shell/sh
                           "sh" "-c"
                           (str move-to-dir (:path path)
                                next-command
                                command))
                         :out) #"\n"))
      (throw (IllegalArgumentException.
               (str (:path path) " is not a directory"))))))


(defmethod cetl-list-file :list-dirs
  [path]
  (let [move-to-dir " cd "
        command  " find `pwd` -type d -maxdepth 1 "
        next-command ";"]
    (if (dir-exists? (:path path))
      (assoc path :result
                  (s/split
                    (get (clojure.java.shell/sh
                           "sh" "-c"
                           (str move-to-dir (:path path)
                                next-command
                                command))
                         :out) #"\n"))
      (throw (IllegalArgumentException.
               (str (:path path) " is not a directory"))))))


(defmethod cetl-list-file :list-dirs-sub-dirs
  [path]
  (let [move-to-dir " cd "
        command  " find `pwd` -type d "
        next-command ";"]
    (if (dir-exists? (:path path))
      (assoc path :result
                  (s/split
                    (get (clojure.java.shell/sh
                           "sh" "-c"
                           (str move-to-dir (:path path)
                                next-command
                                command))
                         :out) #"\n"))
      (throw (IllegalArgumentException.
               (str (:path path) " is not a directory"))))))


(defmethod cetl-list-file :files-only-sub-dirs
  ;TODO implement
  []
  )

#_(defn cetl-file-unarchive
    [path in out & {:keys []}])

;=================================================================================================

(defmulti cetl-archive-file (fn [x] (:exec x)))

(defmethod cetl-archive-file :zip-file
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
    (assoc x :result
             (vector
               (str (:path x) "/" (:file x) file-ext)))))

(defmethod cetl-archive-file :gzip-file
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
    (assoc x :result
             (vector (str (:path x) "/" (:file x) file-ext)))))

;=================================================================================================


(defmulti cetl-encode-file (fn [x] (:exec x)))

(defmethod cetl-encode-file :encode-file-ISO-8859-1
  [x]
  (let [file (:file x)
        path (:path x)
        file-path (str path "/" file)]
    (FileUtils/write
      (File. file-path)
      (FileUtils/readFileToString
        (File. file-path) "UTF-8") "ISO-8859-1"))
  (assoc x :result (vector (str (:path x) "/" (:file x)))))

(defmethod cetl-encode-file :encode-file-UTF-8
  [x]
  (let [file (:file x)
        path (:path x)
        file-path (str path "/" file)]
    (FileUtils/write
      (File. file-path)
      (FileUtils/readFileToString
        (File. file-path) "ISO-8859-1") "UTF-8"))
  (assoc x :result (vector (str (:path x) "/" (:file x)))))


;=================================================================================================

(defmulti cetl-create-temp-file (fn [x] (:exec x)))

(defmethod cetl-create-temp-file :create-temp-file
  [x]
  (let [file (:file x)
        path (:path x)
        full-path (str (:path x) "/" (:file x))]
    (if (dir-exists? path)
      (do
        (io/writer
          (io/file
            (str path "/" file)))
        (assoc x :result (vector full-path)))
      (throw (IllegalArgumentException. (str path " is not a directory"))))))

;================================================================================================

(defmulti cetl-copy-file (fn [x] (:exec x)))

(defmethod cetl-copy-file :copy-file
  [x]
  (let [file (:file x)
        in-path (:in-path x)
        out-path (:out-path x)
        full-in-path (str (:in-path x) "/" (:file x))]
    (cond (not (file-exists? full-in-path))
          (throw
            (IllegalArgumentException.
                   (str full-in-path " file does not exist")))
          (not (dir-exists? in-path))
          (throw
            (IllegalArgumentException.
                   (str in-path " is not a directory")))
          (not (dir-exists? out-path))
          (throw
            (IllegalAccessException.
                   (str out-path " is not a directory")))
          :else
      (do
        (io/copy
          (io/file (str in-path "/" file))
          (io/file (str out-path "/" file)))
        (assoc x :result (vector (str (:out-path x) "/" (:file x))))))))

;================================================================================================

(defn cetl-delete-file
  [x]
  (let [file (:file x)
        path (:path x)
        exec (:exec x)]
    (if (= exec :delete-file)
      (do
        (io/delete-file
          (io/file
            (str path "/" file)))
        (assoc x :result (vector (str (:path x) "/" (:file x))))))))

;================================================================================================


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
        modified-time-str (.format (SimpleDateFormat. "yyyy-MM-dd HH:mm:ss.SSS") modified-time-millis)
        exec (:exec x)]
    (if (= exec :file-properties)
      (do (assoc x :result (vector abs-file-path
                                   parent-dir
                                   file-name
                                   read-permissions
                                   write-permissions
                                   execute-permissions
                                   file-size
                                   modified-time-millis
                                   modified-time-str))))))