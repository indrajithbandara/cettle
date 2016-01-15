(ns cetl.utils.u-path
  (:require [clojure.string :as s]
            [cetl.utils.u-file :refer [in-map]]))

(defn file-dir-paths
  ([] nil)
  ([m]
   (mapcat (fn [s]
             (s/split
               (get (clojure.java.shell/sh
                      "sh" "-c"
                      (str " cd " s ";" " find `pwd` -maxdepth 1 ")) :out) #"\n")) (in-map m))))
(defn file-paths
  ([] nil)
  ([m]
   (mapcat (fn [s] (s/split
                     (get (clojure.java.shell/sh
                            "sh" "-c"
                            (str " cd " s ";"
                                 " find `pwd` -type f -maxdepth 1 ")) :out) #"\n")) (in-map m))))