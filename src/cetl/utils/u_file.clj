(ns cetl.utils.u-file
  (:require [clojure.string :as s]
            [clojure.java.io :as io])
  (import [java.io File]))



(defprotocol FileLike
  (file-name [x] "Returns file name of a given input")
  (parent-path [x] "Returns the directory name of a given input")
  (is-file? [x] "Returns file name of a given input")
  (file-exists? [f] "Returns file name of a given input")
  (is-dir? [x] "Returns file name of a given input")
  (canonical-path [x] "Returns the file path of a given input")
  (abs-file-path [x] "Returns the absolute file path of a given input")
  (abs-file [x] "Returns the absolute file of a given input"))

(extend-protocol
  FileLike
  File
  (file-name [^File file] (.getName file))
  (parent-path [^File file] (.getParent file))
  (is-file? [^File file] (.isFile file))
  (is-dir? [^File file] (.isDirectory file))
  (abs-filepath [^File file] (.getAbsolutePath file))
  (abs-file [^File file] (.getAbsoluteFile file))
  (file-exists? [^File file] (.exists file))
  (canonical-path [^File file] (.getCanonicalPath file)))


(defn in-map
  ([] nil)
  ([x]
   (cond (= (keys x) '(:in))
         (:in x)
         (= (keys x) '(:out))
         (:out x)
          :else nil)))

(defn all-files-exist?
  ([] nil)
  ([m] (nil? (some false?
                   (map #(file-exists? (io/file %)) (in-map m))))))