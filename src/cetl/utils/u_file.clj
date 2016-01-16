(ns cetl.utils.u-file
  (:require [clojure.string :as s]
            [clojure.java.io :as io])
  (import [java.io File]))


(def throw-exception
  {:in-no-map-found (IllegalArgumentException. (str "in requires a map of the form {:in ...}"))
   :in-no-key-found (IllegalArgumentException. (str "in requires :in as key that references input"))})

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


(defn in
  [x]
  (cond (false? (map? x))
        (IllegalArgumentException.
          (str "in requires a map of the form {:in ...}"))
        (= (keys x) '(:in)) (:in x)
        (= (keys x) '(:out)) (:out x)
        :else (IllegalArgumentException.
                (str "in requires :in as key that references input"))))

(defn check-all-file
  [p? f m] (nil? (some false?
                  (map #(p? (io/file %)) (f m)))))