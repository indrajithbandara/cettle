(ns cetl.utils.component-utils
  (:require [clojure.string :as s]
            [clojure.java.io :as io])
  (import [java.io File]))



(defprotocol FileLike
  (file-name [x] "Returns file name of a given input")
  (parent-path [x] "Returns the directory name of a given input")
  (is-file? [x] "Returns file name of a given input")
  (exists-file? [x] "Returns file name of a given input")
  (is-dir? [x] "Returns file name of a given input")
  (file-path [x] "Returns the file path of a given input")
  (abs-file-path [x] "Returns the absolute file path of a given input")
  (abs-file [x] "Returns the absolute file of a given input")
  (file-exists? [x] "Returns true or false if a file exists"))

(extend-protocol
  FileLike
  File
  (file-name [^File file] (.getName file))
  (parent-path [^File file] (.getParentFile file))
  (is-file? [^File file] (.isFile file))
  (exists-file? [^File file] (.exists file))
  (is-dir? [^File file] (.isDirectory file))
  (abs-file-path [^File file] (.getAbsolutePath file))
  (abs-file [^File file] (.getAbsoluteFile file))
  (file-exists? [^File file] (.exists file)))

(defn path-from-map
  [x]
  (io/file (:path x)))

(defn file-exists
  [path]
  (cond (map? path)
    (let [file (io/file (str (:path path) "/" (:file path)))]
      (if (and (exists-file? file) (is-file? file))
        path
        (throw
          (IllegalArgumentException.
            (str file " is not a file")))))
        (string? path)
        (let [file (io/file path)]
          (if (and (exists-file? file) (is-file? file))
            path
            (throw
              (IllegalArgumentException.
                (str path " is not a file")))))
        :else nil))


(defn dir-exists?
  [path]
  (cond
    (map? path)
    (let [file (io/file (:path path))]
      (if (and (exists-file? file) (is-dir? file))
        path
        (throw
          (IllegalArgumentException.
            (str path " is not a directory")))))
    (string? path)
    (let [file (io/file path)]
      (if (and (exists-file? file) (is-dir? file))
        path
        (throw
          (IllegalArgumentException.
            (str path " is not a directory")))))))

