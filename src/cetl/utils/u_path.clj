(ns cetl.utils.u-path
  (:require [clojure.string :as s]))

(defn dir-path
  [f m]
  (mapcat (fn [s]
            (s/split
              (get (clojure.java.shell/sh
                     "sh" "-c"
                     (str " cd " s ";"
                          " find `pwd` -maxdepth 1 "))
                   :out) #"\n")) (f m)))
(defn path
  [f m]
  (mapcat (fn [s] (s/split
                    (get (clojure.java.shell/sh
                           "sh" "-c"
                           (str " cd " s ";"
                                " find `pwd` -type f -maxdepth 1 "))
                         :out) #"\n")) (f m)))
(defn dir
  [f m]
  (mapcat (fn [s] (s/split
        (get (clojure.java.shell/sh
               "sh" "-c"
               (str " cd " s ";"
                    " find `pwd` -type d -not -path '*/\\.*' -maxdepth 1 "))
             :out) #"\n")) (f m)))