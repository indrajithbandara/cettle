(ns cetl.file.utils.command-utils
  (:require [clojure.java.io :as io]
            [clojure.string :as s]
            [cetl.file.utils.file-utils :refer [parent-path file-name in-from-map]]))


(defn zip-command
  ([] nil)
  ([v] (into {} (map (fn [s]
                       (clojure.java.shell/sh
                         "sh" "-c"
                         (str " cd " (parent-path (io/file s)) ";" " zip " (file-name (io/file s)) ".zip" " -r " (file-name (io/file s))))) v))))

(defn gzip-command
  ([] nil)
  ([l] (into {} (map (fn [s]
                       (clojure.java.shell/sh
                         "sh" "-c"
                         (str " cd " (parent-path (io/file s)) ";" " tar -cvzf " (file-name (io/file s)) ".tar.gz " (file-name (io/file s))))) l))))

(defn build-zip-output-map
  ([] nil)
  ([f l] (assoc (empty f) :out (map #(str % ".zip") l) :exec :cetl-zip-file)))

(defn build-gzip-output-map
  ([] nil)
  ([f l] (assoc (empty f) :out (map #(str % ".tar.gz") l) :exec :cetl-gzip-file)))

(defn list-dirs-files-command
  ([] nil)
  ([m]
   (mapcat (fn [s]
             (s/split
               (get (clojure.java.shell/sh
                      "sh" "-c"
                      (str " cd " s ";" " find `pwd` -maxdepth 1 ")) :out) #"\n")) (in-from-map m))))