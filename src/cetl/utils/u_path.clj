(ns cetl.utils.u-path
  (:require [clojure.string :as s]))


(defn command-xform->map
  [p]
  (comp
    (mapcat (fn [s]
              (s/split
                (get (clojure.java.shell/sh
                       "sh" "-c"
                       (str " cd " s ";" p)) :out) #"\n")))))


#_(defn path
  ([k]
   (fn
     ([] k)
     ([m] (mapcat (fn [s] (s/split
                            (get (clojure.java.shell/sh
                                   "sh" "-c"
                                   (str " cd " s ";"
                                        " find `pwd` -type f -maxdepth 1 "))
                                 :out) #"\n")) (k m))))))
#_(defn dir
  [f m]
  (mapcat (fn [s] (s/split
        (get (clojure.java.shell/sh
               "sh" "-c"
               (str " cd " s ";"
                    " find `pwd` -type d -not -path '*/\\.*' -maxdepth 1 "))
             :out) #"\n")) (f m)))

#_(defn dir-sub
  [f m]
  (mapcat (fn [s] (s/split
                    (get (clojure.java.shell/sh
                           "sh" "-c"
                           (str " cd " s ";"
                                " find `pwd` -type d "))
                         :out) #"\n")) (f m)))