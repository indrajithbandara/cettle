(ns cetl.file.management
  (:require [clojure.string :as s]
            [clojure.java.io :as io]
            [clojure.set :refer [rename-keys]]
            [cetl.utils.u-file :refer [file-exists? file-name is-dir? parent-path check-path]]
            [cetl.utils.u-compress :refer [zip gzip gzip-map zip-map]]
            [cetl.utils.u-path :refer [exec-command->map]])
  (:import (java.io File LineNumberReader)
           (org.apache.commons.io FileUtils)
           (java.text SimpleDateFormat)))
(use '[clojure.java.shell :only [sh]]
     'com.rpl.specter)

(def command->map {:file-dir " find `pwd` -maxdepth 1 "
                   :file " find `pwd` -type f -maxdepth 1 "
                   :dir " find `pwd` -type d -not -path '*/\\.*' -maxdepth 1 "
                   :dir-sub " find `pwd` -type d "})

(defn cetl-path
  ([k]
   (fn [x]
     {:path (transduce (exec-command->map (k command->map)) conj '() x) :exec :cetl-list-file})))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(transform [:path ALL ALL string?] zip path)

(defn cetl-zip [f]
  (fn [m]
    (if ((check-path file-exists?) m)
      (let [path (:path m)]
        (assoc (empty (f path))
          :zip-path (map #(str % ".zip") path) :exec :cetl-zip-file)))))


#_(defn cetl-gzip
  ([] nil)
  ([m]
   (if ((check-file file-exists?) m)
     (let [path (in m)]
       (assoc (empty (gzip path))
         :out (map #(str % ".tar.gz") path) :exec :cetl-gzip-file)))))


  #_(defn cetl-encode-ISO8859-1
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


  #_(defn cetl-encode-UTF-8
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

#_(defn cetl-copy-file
  ([] nil)
  ([x] (cond (nil? x) nil
             (= (count (:path x)) 2)
             (let [pathv (:path x)
                   in-path (io/file (first pathv))
                   out-path (io/file (second pathv))
                   fname (file-name in-path)]
               (if (and (file-exists? in-path)
                        (is-dir? out-path))
                 (do
                   (io/copy in-path (io/file (str out-path "/" fname)))
                   (assoc x :path (str out-path "/" (file-name in-path)))))))))


#_(defn cetl-delete-file
  [x]
  (let [file (:file x)
        path (:path x)
        file-path (str path "/" file)]
    (if (file-exists? file-path)
      (do
        (io/delete-file (io/file (str path "/" file)))
        (assoc x :result (str (:path x) "/" (:file x)))))))

#_(defn cetl-properties-file
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

#_(defn cetl-compare-file
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

#_(defn cetl-count-row-file
  [x]
  (let [file (:file x)
        path (:path x)
        file-path (str path "/" file)
        line-num-reader (-> (io/file file-path) (io/reader) (LineNumberReader.))]
    (if (file-exists? file-path)
      (do (.skip line-num-reader Long/MAX_VALUE)
          (.close line-num-reader)
          (assoc x :result  (.getLineNumber line-num-reader))))))



#_(defn cetl-gpg-encrypt-file
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

#_(defn zip-command
    [po]
    (map (fn [y]
           (clojure.java.shell/sh
             "sh" "-c"
             (str " cd " (parent-path y) ";"
                  " zip " (file-name y) ".zip"
                  " -r " (file-name y)))) po))


#_(defn cetl-zip-file
    [x]
    (if (nil? x) nil (let [path (path-from-map x)
                       path-obj (map #(io/file %) path)]
                   (do (zip-command path-obj)
                       (assoc x
                         :path (mapv #(str % ".zip") path)
                         :exec :cetl-zip-file)))))

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

(defn rf
  [rf result]
  (fn
    ([] result) ;resulting collection if any
    ([x] x)                                               ;return type
    ([x input] (rf x input)))) ;reducing stepd

(def xform (map str))

(transduce xform conj '() '(1 3 2))

#_(transduce xform conj '(1 3 2))

#_(transduce xform conj '() '(1 3 2))