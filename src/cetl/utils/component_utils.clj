(ns cetl.utils.component-utils
  (:require [clojure.string :as s])
  (import [java.io File]))


(defn file-exists?
  [path]
  (let [file (File. path)]
    (if (or (and (.exists file)
                 (not (.isDirectory file))))
      path)))

