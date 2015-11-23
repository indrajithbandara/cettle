(ns cetl.file.management
  (:require [clojure.string :as s]
            [clojure.java.io :as io]
            [clojure.set :refer [rename-keys]]
            [cetl.utils.component-utils :refer [file-exists? dir-exists?]])
  (:import (org.apache.commons.io FileUtils)
           (java.io File LineNumberReader FileReader)
           (java.text SimpleDateFormat)))
(use '[clojure.java.shell :only [sh]])


(defmulti cetl-list-file {:arglists '([map])}
          (fn [x] (:exec x)))

(defmethod cetl-list-file :list-dirs-files
  [x]
  (let [move-to-dir " cd "
        command " find `pwd` -maxdepth 1 "
        next-command ";"]
    (if (dir-exists? (:path x))
      (assoc x
        :result (s/split
                  (get (clojure.java.shell/sh
                         "sh" "-c"
                         (str move-to-dir (:path x)
                              next-command
                              command)) :out) #"\n"))
      (throw
        (IllegalArgumentException.
          (str (:path x) " is not a directory"))))))


(defmethod cetl-list-file :list-files
  [x]
  (let [move-to-dir " cd "
        command  " find `pwd` -type f -maxdepth 1 "
        next-command ";"]
    (if (dir-exists? (:path x))
      (assoc x
        :result (s/split
                  (get (clojure.java.shell/sh
                         "sh" "-c"
                         (str move-to-dir (:path x)
                              next-command
                              command)) :out) #"\n"))
      (throw
        (IllegalArgumentException.
               (str (:path x) " is not a directory"))))))


(defmethod cetl-list-file :list-dirs
  [x]
  (let [move-to-dir " cd "
        command  " find `pwd` -type d -maxdepth 1 "
        next-command ";"]
    (if (dir-exists? (:path x))
      (assoc x
        :result (s/split
                  (get (clojure.java.shell/sh
                         "sh" "-c"
                         (str move-to-dir (:path x)
                              next-command
                              command)) :out) #"\n"))
      (throw
        (IllegalArgumentException.
               (str (:path x) " is not a directory"))))))


(defmethod cetl-list-file :list-dirs-sub-dirs
  [x]
  (let [move-to-dir " cd "
        command  " find `pwd` -type d "
        next-command ";"]
    (if (dir-exists? (:path x))
      (assoc x
        :result (s/split
                  (get (clojure.java.shell/sh
                         "sh" "-c"
                         (str move-to-dir (:path x)
                              next-command
                              command)) :out) #"\n"))
      (throw
        (IllegalArgumentException.
               (str (:path x) " is not a directory"))))))


(defmethod cetl-list-file :files-only-sub-dirs
  ;TODO implement
  []
  )

#_(defn cetl-file-unarchive
    [path in out & {:keys []}])


(defmulti cetl-archive-file  {:arglists '([map])}
          (fn [x] (:exec x)))


(defmethod cetl-archive-file :zip-file
  [x]
  (let [move-to-dir " cd "
        zip-command " zip "
        rec-command " -r "
        next-command ";"
        file-ext ".zip"
        path (:path x)
        file (:file x)
        file-path (str path "/" file)]
    (if (or (file-exists? file-path) (dir-exists? path))
      (do
        (clojure.java.shell/sh
          "sh" "-c"
          (str move-to-dir (File. path) next-command
               zip-command (str file file-ext)
               rec-command file))
        (assoc x
          :result (str file-path file-ext)))
      (throw
        (IllegalArgumentException.
          (str file-path " is not a file (or a directory)"))))))


(defmethod cetl-archive-file :gzip-file
  [x]
  (let [move-to-dir " cd "
        gzip-command " tar -cvzf "
        next-command ";"
        file-ext ".tar.gz"
        path (:path x)
        file (:file x)
        file-path (str path "/" file)]
    (if (or (file-exists? file-path) (dir-exists? path))
      (do
        (clojure.java.shell/sh
          "sh" "-c"
          (str move-to-dir (File. path) next-command
               gzip-command (str file file-ext " " file)))
        (assoc x
          :result (str file-path file-ext)))
      (throw
        (IllegalArgumentException.
          (str file-path " is not a file (or a directory)"))))))


(defmulti cetl-encode-file {:arglists '([map])}
          (fn [x] (:exec x)))

(defmethod cetl-encode-file :ISO-8859-1
  [x]
  (let [file (:file x)
        path (:path x)
        file-path (str path "/" file)]
    (if (file-exists? file-path)
      (do
        (FileUtils/write (File. file-path)
          (FileUtils/readFileToString
            (File. file-path) "UTF-8") "ISO-8859-1")
        (assoc x
          :result (str (:path x) "/" (:file x))))
      (throw
        (IllegalArgumentException.
          (str file-path " is not a file (or a directory)"))))))


(defmethod cetl-encode-file :UTF-8
  [x]
  (let [file (:file x)
        path (:path x)
        file-path (str path "/" file)]
    (if (file-exists? file-path)
      (do
        (FileUtils/write (File. file-path)
          (FileUtils/readFileToString
            (File. file-path) "ISO-8859-1") "UTF-8")
        (assoc x :result (str (:path x) "/" (:file x))))
      (throw
        (IllegalArgumentException.
          (str file-path " is not a file (or a directory)"))))))


(defmulti cetl-create-temp-file {:arglists '([map])}
          (fn [x] (:exec x)))

(defmethod cetl-create-temp-file :create-temp-file
  [x]
  (let [file (:file x)
        path (:path x)
        full-path (str (:path x) "/" (:file x))]
    (if (dir-exists? path)
      (do
        (io/writer (io/file (str path "/" file)))
        (assoc x :result full-path))
      (throw
        (IllegalArgumentException.
          (str path " is not a directory"))))))


(defmulti cetl-copy-file {:arglists '([map])}
          (fn [x] (:exec x)))

(defmethod cetl-copy-file :copy-file
  [x]
  (let [file (:file x)
        in-path (:in-path x)
        out-path (:out-path x)
        full-in-path (str (:in-path x) "/" (:file x))]
    (cond (not (file-exists? full-in-path))
          (throw
            (IllegalArgumentException.
              (str full-in-path " is not a file (or directory)")))
          (not (dir-exists? out-path))
          (throw
            (IllegalArgumentException.
              (str out-path " is not a directory")))
          :else
          (do
            (io/copy
              (io/file (str in-path "/" file))
              (io/file (str out-path "/" file)))
            (assoc x :result (str (:out-path x) "/" (:file x)))))))


(defmulti cetl-delete-file {:arglists '([map])}
          (fn [x] (:exec x)))

(defmethod cetl-delete-file :delete-file
  [x]
  (let [file (:file x)
        path (:path x)
        file-path (str path "/" file)]
    (if (file-exists? file-path)
      (do
        (io/delete-file (io/file (str path "/" file)))
        (assoc x :result (str (:path x) "/" (:file x))))
      (throw
        (IllegalArgumentException.
          (str file-path " is not a file (or a directory)"))))))


(defmulti cetl-properties-file {:arglists '([map])}
          (fn [x] (:exec x)))

(defmethod cetl-properties-file :properties-file
  [x]
  (let [file (io/file (str (:path x) "/" (:file x)))
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
    (if (file-exists? (.getAbsolutePath file))
      (do (assoc x :result {:abs-file-path abs-file-path
                            :parent-dir parent-dir
                            :file-name file-name
                            :read-permissions read-permissions
                            :write-permissions write-permissions
                            :execute-permissions execute-permissions
                            :file-size file-size
                            :modified-time-millis modified-time-millis
                            :modified-time-str modified-time-str}))
      (throw
        (IllegalArgumentException.
          (str file " is not a file (or a directory)"))))))


(defmulti cetl-compare-file {:argslist '([map])}
          (fn [x] (:exec x)))

(defmethod cetl-compare-file :compare-file
  [x]
  (let [file-one (:file-one x)
        path-one (:path-one x)
        file-path-one (File. (str path-one "/" file-one))
        file-two (:file-two x)
        path-two (:path-two x)
        file-path-two (File. (str path-two "/" file-two))]
    (cond
      (not (file-exists?
             (.getAbsolutePath file-path-one)))
          (throw
            (IllegalArgumentException.
              (str file-path-one
                   " is not a file (or a directory)")))
          (not (file-exists?
                 (.getAbsolutePath file-path-two)))
          (throw
            (IllegalArgumentException.
              (str file-path-two
                   " is not a file (or a directory)")))
      :else (assoc x :result (FileUtils/contentEquals file-path-one file-path-two)))))


(defmulti cetl-count-row-file {:argslist '([map])}
          (fn [x] (:exec x)))

(defmethod cetl-count-row-file :count-row-file
  [x]
  (let [file (:file x)
        path (:path x)
        file-path (str path "/" file)
        line-num-reader (-> (io/file file-path) (io/reader) (LineNumberReader.))]
    (if (file-exists? file-path)
      (do (.skip line-num-reader Long/MAX_VALUE)
          (+ 1 (.getLineNumber line-num-reader)))
      (throw
        (IllegalArgumentException.
          (str file-path " is not a file (or a directory)"))
        (finally
          (.close line-num-reader))))))


(defmulti cetl-touch-file {:argslist '([map])}
          (fn [x] (:exec x)))

(defmethod cetl-touch-file :touch-file
  [x]
  (let [file (:file x)
        path (:path x)
        file-path (str path "/" file)]
    (if (file-exists? file-path)
      ())))

