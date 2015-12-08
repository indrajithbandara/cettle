(ns cetl.file.management
  (:require [clojure.string :as s]
            [clojure.java.io :as io]
            [clojure.set :refer [rename-keys]]
            [cetl.utils.component-utils :refer [file-exists? dir-exists?]])
  (:import (org.apache.commons.io FileUtils)
           (java.util Date)
           (java.io File LineNumberReader)
           (java.text SimpleDateFormat)))
(use '[clojure.java.shell :only [sh]])

;TODO  migrate rest of functions to defprotocol implementations
;TODO  create macro for file and dir checking to pull out noisy code form funcs (if-file (do ....))
;TODO Alter file-exists func to dynamically work with url or map

  (defn cetl-zip-file [x]
    [x]
    (if (or (file-exists? x)
            (dir-exists? (:path x)))
      (let [path (:path x)
            file (:file x)
            file-path (str path "/" file)]
        (do
          (clojure.java.shell/sh
            "sh" "-c"
            (str " cd " path ";" " zip " file ".zip" " -r " file))
          (assoc x
            :result (str file-path ".zip")
            :exec :zip-file)))
      (throw
        (IllegalArgumentException.
          (str (:path x) "/" (:file x) " is not a file (or a directory)")))))


  (defn cetl-gzip-file
    [x]
    (if (or (file-exists? x)
            (dir-exists? (:path x)))
      (let [path (:path x)
            file (:file x)
            file-path (str path "/" file)]
        (do
          (clojure.java.shell/sh
            "sh" "-c"
            (str " cd " path ";"
                 " tar -cvzf " (str file ".tar.gz " file)))
          (assoc x
            :result (str file-path ".tar.gz")
            :exec :gzip)))
      (throw
        (IllegalArgumentException.
          (str (:file x) "/" (:path x) " is not a file (or a directory)")))))


  (defn list-dirs-files
    [x]
    (if (dir-exists? (str (:path x) "/" (:file x)))
      (let [file-path (str (:path x) "/" (:file x))]
        (assoc x
          :result (s/split
                    (get (clojure.java.shell/sh
                           "sh" "-c"
                           (str " cd " file-path ";"
                                " find `pwd` -maxdepth 1 ")) :out) #"\n")
          :exec :list-dirs-files))
      (throw
        (IllegalArgumentException.
          (str (:path x) "/" (:file x) " is not a directory")))))


  (defn cetl-list-files
    [x]
    (if (dir-exists? (str (:path x) "/" (:file x)))
      (let [file-path (str (:path x) "/" (:file x))]
        (assoc x
          :result (s/split
                    (get (clojure.java.shell/sh
                           "sh" "-c"
                           (str " cd " file-path ";"
                                " find `pwd` -type f -maxdepth 1 ")) :out) #"\n")
          :exec :list-files))
      (throw
        (IllegalArgumentException.
          (str (:path x) "/" (:file x) " is not a directory")))))


  (defn cetl-list-dirs
    [x]
    (if (dir-exists? (str (:path x) "/" (:file x)))
      (let [file-path (str (:path x) "/" (:file x))]
        (assoc x
          :result (s/split
                    (get (clojure.java.shell/sh
                           "sh" "-c"
                           (str " cd " file-path ";"
                                " find `pwd` -type d -not -path '*/\\.*' -maxdepth 1 ")) :out) #"\n")
          :exec :list-dirs))
      (throw
        (IllegalArgumentException.
          (str (str (:path x) "/" (:file x)) " is not a directory")))))


  (defn cetl-list-dirs-sub-dirs
    [x]
    (if (dir-exists? (str (:path x) "/" (:file x)))
      (let [file-path (str (:path x) "/" (:file x))]
        (assoc x
          :result (s/split
                    (get (clojure.java.shell/sh
                           "sh" "-c"
                           (str " cd " file-path ";"
                                " find `pwd` -type d ")) :out) #"\n")
          :exec :list-dirs-sub-dirs))
      (throw
        (IllegalArgumentException.
          (str (:path x) "/" (:file x) " is not a directory")))))


  (defn cetl-encode-ISO8859-1
    [x]
    (if (file-exists? x)
      (let [file-path (str (:path x) "/" (:file x))]
        (do
          (FileUtils/write (File. file-path)
                           (FileUtils/readFileToString
                             (File. file-path) "UTF-8") "ISO-8859-1")
          (assoc x
            :result file-path
            :exec :encode-ISO8859-1)))
      (throw
        (IllegalArgumentException.
          (str (:path x) "/" (:file x) " is not a file (or a directory)")))))


  (defn cetl-encode-UTF-8
    [x]
    (if (file-exists? x)
      (let [file (:file x)
            path (:path x)
            file-path (str path "/" file)]
        (do
          (FileUtils/write (File. file-path)
                           (FileUtils/readFileToString
                             (File. file-path) "ISO-8859-1") "UTF-8")
          (assoc x :result file-path
                   :exec :encode-UTF-8)))
      (throw
        (IllegalArgumentException.
          (str (:path x) "/" (:file x) " is not a file (or a directory)")))))

(defn cetl-copy-file
  [x]
  (let [file (:file x)
        path (:path x)
        full-in-path (str (first path) "/" file)
        in-path (first path)
        out-path (second path)]
    (cond (not (.exists (File. full-in-path)))
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
            (update
              (assoc x :result (str (second (:path x)) "/" (:file x)))
              :path (first (:path x)))))))                  ;;TODO fix to accept first elem in vec


(defn cetl-copy-file
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


(defn cetl-delete-file
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

(defmethod cetl-file-management :properties-file
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

(defmethod cetl-file-management :compare-file
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

(defmethod cetl-file-management :count-row-file
  [x]
  (let [file (:file x)
        path (:path x)
        file-path (str path "/" file)
        line-num-reader (-> (io/file file-path) (io/reader) (LineNumberReader.))]
    (if (file-exists? file-path)
      (do (.skip line-num-reader Long/MAX_VALUE)
          (assoc x :result (+ 1 (.getLineNumber line-num-reader))))
      (throw
        (IllegalArgumentException.
          (str file-path " is not a file (or a directory)"))
        (finally
          (.close line-num-reader))))))

(defmethod cetl-file-management :touch-file
  [x]
  (let [file (:file x)
        path (:path x)
        file-path (str path "/" file)]
    (if (file-exists? file-path)
      (assoc x :result
               (.setLastModified (File. file-path) (.getTime (Date.))))
      (do
        (cetl-file-management {:file file :path path :exec :create-temp-file})
        (assoc x :result file-path)))))

(defmethod cetl-file-management :gpg-encrypt-file
  [x]
  (if (file-exists? (str (:path x) "/" (:file x)))
    (let [recipient (:recipient x)
          file (:file x)
          move-to-dir " cd "
          command (str " gpg --recipient "recipient" --encrypt "file)
          next-command ";"]
      (do
        (get (clojure.java.shell/sh
               "sh" "-c"
               (str move-to-dir (:path x)
                    next-command
                    command)) :out)
        (assoc x :result (str (:path x) "/" (:file x) ".gpg"))))
    (throw
      (IllegalArgumentException.
        (str (:path x) "/" (:file x) " is not a file (or a directory)")))))
