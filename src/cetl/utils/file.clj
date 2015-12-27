(ns cetl.utils.file
  (:require [clojure.string :as s]
            [clojure.java.io :as io])
  (import [java.io File]))



(defprotocol FileLike
  (fileName [x] "Returns file name of a given input")
  (parentPath [x] "Returns the directory name of a given input")
  (isFile? [x] "Returns file name of a given input")
  (fileExists? [f] "Returns file name of a given input")
  (isDir? [x] "Returns file name of a given input")
  (canonicalPath [x] "Returns the file path of a given input")
  (absFilePath [x] "Returns the absolute file path of a given input")
  (absFile [x] "Returns the absolute file of a given input"))

(extend-protocol
  FileLike
  File
  (fileName [^File file] (.getName file))
  (parentPath [^File file] (.getParent file))
  (isFile? [^File file] (.isFile file))
  (isDir? [^File file] (.isDirectory file))
  (absFilePath [^File file] (.getAbsolutePath file))
  (absFile [^File file] (.getAbsoluteFile file))
  (fileExists? [^File file] (.exists file))
  (canonicalPath [^File file] (.getCanonicalPath file)))

(defn file-exists?
  ([] nil)
  ([x] (fileExists? (io/file x))))

(defn parent-path
  ([] nil)
  ([x] (parentPath (io/file x))))

(defn file-name
  ([] nil)
  ([x] (fileName (io/file x))))

(defn path-from-map [x] (:path x))

