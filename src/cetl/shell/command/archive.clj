(ns cetl.shell.command.archive
  (:require [clojure.java.io :as io]
            [clojure.string :as s]
            [cetl.utils.file :refer [parent-path file-name]]))


(defrecord CompressionFormat [zip gzip])

(defn compress-command
  [f]
  (CompressionFormat. (str " zip " f ".zip -r " f)
                      (str " tar -cvzf " f ".tar.gz " f)))

#_(defn zip
  [l]
  (into {} (map (fn [s]
                  (exec
                    "sh" "-c"
                    (str " cd " (parent-path (io/file s)) ";"
                         " zip " (file-name (io/file s)) ".zip"
                         " -r " (file-name (io/file s))))) l)))

(def )

(defn zip
  [s]
  (exec
    "sh" "-c"
    (str " cd " (parent-path (io/file s)) ";"
         " zip " (file-name (io/file s)) ".zip"
         " -r " (file-name (io/file s)))))

(defn gzip
   [l] (into {} (map (fn [s]
                       (exec
                         "sh" "-c"
                         (str " cd " (parent-path (io/file s)) ";" " tar -cvzf " (file-name (io/file s)) ".tar.gz " (file-name (io/file s))))) l)))

(defn zip-map
  [f l]
  (assoc (empty f) :out (map #(str % ".zip") l) :exec :cetl-zip-file))

(defn gzip-map
  [f l]
  (assoc (empty f) :out (map #(str % ".tar.gz") l) :exec :cetl-gzip-file))



