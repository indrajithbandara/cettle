(ns cetl.utils.file
  (:require [clojure.string :as s]
            [clojure.java.io :as io])
  (import [java.io File]))

(defprotocol FileLike
  (file-name [x] "Returns file name of x")
  (parent-path [x] "Returns the directory name of x")
  (is-file? [x] "Returns true if x is a file, else false")
  (file-exists? [f] "Returns true if x exists, else false")
  (is-dir? [x] "Returns true if x is a directory, else false")
  (canonical-path [x] "Returns the file path x")
  (abs-file-path [x] "Returns the absolute file path of x")
  (abs-file [x] "Returns the absolute file path object of x")
  (is-hidden? [x] "Returns true if x is a hidden file, else false"))

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
  (canonical-path [^File file] (.getCanonicalPath file))
  (is-hidden? [^File file] (.isHidden file))
  String
  (is-hidden? [^String file] (.isHidden (File. file)))
  (file-name [^String file] (.getName (File. file)))
  (parent-path [^String file] (.getParent (File. file))))

(defn check-path
  [f?]
  (fn [m]
    (nil? (some false?
                (map #(f? (io/file %))
                     (cond (false? (map? m))
                           (IllegalArgumentException.
                             (str "in requires a map of the form {:in ...}"))
                           (= (keys m) '(:path)) (:path m)
                           :else (IllegalArgumentException.
                                   (str "in requires :in as key that references input"))))))))