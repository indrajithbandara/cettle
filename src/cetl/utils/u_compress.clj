(ns cetl.utils.u-compress
  (:require [clojure.java.io :as io]
            [clojure.string :as s]
            [cetl.utils.u-file :refer [parent-path file-name]]))

(defn zip
  [l]
  (into {} (map (fn [s]
                  (clojure.java.shell/sh
                    "sh" "-c"
                    (str " cd " (parent-path (io/file s)) ";" " zip " (file-name (io/file s)) ".zip" " -r " (file-name (io/file s))))) l)))

(defn zip
  [s]
  (clojure.java.shell/sh
    "sh" "-c"
    (str " cd " (parent-path (io/file s)) ";"
         " zip " (file-name (io/file s)) ".zip"
         " -r " (file-name (io/file s)))))

(defn gzip
   [l] (into {} (map (fn [s]
                       (clojure.java.shell/sh
                         "sh" "-c"
                         (str " cd " (parent-path (io/file s)) ";" " tar -cvzf " (file-name (io/file s)) ".tar.gz " (file-name (io/file s))))) l)))

(defn zip-map
  [f l]
  (assoc (empty f) :out (map #(str % ".zip") l) :exec :cetl-zip-file))

(defn gzip-map
  [f l]
  (assoc (empty f) :out (map #(str % ".tar.gz") l) :exec :cetl-gzip-file))
