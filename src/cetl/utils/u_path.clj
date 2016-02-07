(ns cetl.utils.u-path
  (:require [clojure.string :as s]
            [clojure.contrib.shell :refer parse-args ]))

(defn exec-command->map
  [p]
  (map (fn [s]
         (s/split
           (get (clojure.java.shell/sh
                  "sh" "-c"
                  (str " cd " s ";" p)) :out) #"\n"))))