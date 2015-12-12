(ns cetl.file.management
  (:require [clojure.string :as s]
            [clojure.java.io :as io]
            [clojure.set :refer [rename-keys]]
            [cetl.utils.component-utils :refer [file-exists? dir-exists? file-name
                                                path-from-map parent-path]])
  (:import (org.apache.commons.io FileUtils)
           (java.util Date)
           (java.io File LineNumberReader)
           (java.text SimpleDateFormat)))
(use '[clojure.java.shell :only [sh]])

;TODO  migrate rest of functions to defprotocol implementations
;TODO  create macro for file and dir checking to pull out noisy code form funcs (if-file (do ....))
;TODO Alter file-exists func to dynamically work with url or map
;TODO remove excption from funcs as fil-exists? and dir-exists? is now dynamic
;TODO CHANGE :path and :file to just :path where :path contains path + file this means

  (defn cetl-zip-file [x]
    [x]
    (if (file-exists? (path-from-map x))
      (let [path (path-from-map x)
            file-path (parent-path path)
            file-name (file-name path)]
        (do
          (clojure.java.shell/sh
            "sh" "-c"
            (str " cd " file-path ";" " zip " file-name ".zip" " -r " file-name))
          (assoc x
            :path (str path ".zip")
            :exec :zip-file)))))


  (defn cetl-gzip-file
    [x]
    (if (file-exists? (path-from-map x))
      (let [path (path-from-map x)
            file-path (parent-path path)
            file-name (file-name path)]
        (do
          (clojure.java.shell/sh
            "sh" "-c"
            (str " cd " file-path ";"
                 " tar -cvzf " (str file-name ".tar.gz " file-name)))
          (assoc x
            :path (str path ".tar.gz")
            :exec :gzip)))))


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
          :exec :list-dirs-files))))


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
          :exec :list-files))))


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
          :exec :list-dirs))))


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
          :exec :list-dirs-sub-dirs))))


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
            :exec :encode-ISO8859-1)))))


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
                   :exec :encode-UTF-8)))))

(defn cetl-copy-file
  [x]
  (let [file (:file x)
        path (:path x)
        full-in-path (str (first path) "/" file)
        in-path (first path)
        out-path (second path)]
    (if (and (file-exists? full-in-path)
             (dir-exists? in-path)
             (dir-exists? out-path))
      (do
        (io/copy
          (io/file (str in-path "/" file))
          (io/file (str out-path "/" file)))
        (assoc x :result (str (second (:path x)) "/" (:file x)))))))


(defn cetl-delete-file
  [x]
  (let [file (:file x)
        path (:path x)
        file-path (str path "/" file)]
    (if (file-exists? file-path)
      (do
        (io/delete-file (io/file (str path "/" file)))
        (assoc x :result (str (:path x) "/" (:file x)))))))

(defn cetl-properties-file
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
                            :modified-time-str modified-time-str})))))

(defn cetl-compare-file
  [x]
  (let [file-one (:file-one x)
        path-one (:path-one x)
        file-path-one (File. (str path-one "/" file-one))
        file-two (:file-two x)
        path-two (:path-two x)
        file-path-two (File. (str path-two "/" file-two))]
    (if
      (and (file-exists? (.getAbsolutePath file-path-one))
           (file-exists? (.getAbsolutePath file-path-two)))
      (assoc x :result (FileUtils/contentEquals file-path-one file-path-two)))))

(defn cetl-count-row-file
  [x]
  (let [file (:file x)
        path (:path x)
        file-path (str path "/" file)
        line-num-reader (-> (io/file file-path) (io/reader) (LineNumberReader.))]
    (if (file-exists? file-path)
      (do (.skip line-num-reader Long/MAX_VALUE)
          (.close line-num-reader)
          (assoc x :result  (.getLineNumber line-num-reader))))))

#_(defn cetl-touch-file
  [x]
  (let [file (:file x)
        path (:path x)
        file-path (str path "/" file)]
    (if (file-exists? file-path)
      (assoc x :result
               (.setLastModified (File. file-path) (.getTime (Date.))))
      (do
        (cetl-create-temp-file {:file file :path path})
        (assoc x :result file-path)))))

(defn cetl-gpg-encrypt-file
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
        (assoc x :result (str (:path x) "/" (:file x) ".gpg"))))))
