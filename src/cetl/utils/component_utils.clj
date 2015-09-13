(ns cetl.utils.component-utils
  (:require [clojure.string :as s])
  (:import (java.util.zip ZipFile)
           (java.io File)))


(defn path-head
  "Returns the head of a
   file path"
  [x]
  (last (s/split x #"/")))

(defn parent-folder
  "Returns the parent
   folder of a given
   file path"
  [x]
  (str (.getParent (File. x)) "/"))

(defn set-schema [])

(defn set-encoding [])


(defn list-entries
  "Return a set of entry names"
  [file]
  (map
    #(.getName %)
       (enumeration-seq
         (.entries
           (new ZipFile file)))))

(defn list-files
  "Return only files, ignore directories"
  [file]
  (filter
    #(not= \/ (last %)) (list-entries file)))


