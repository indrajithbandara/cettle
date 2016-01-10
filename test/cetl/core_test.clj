(ns cetl.core-test
  (:require [clojure.test :refer :all]
            [cetl.core :refer :all]
            [clojure.java.io :as io]
            [cetl.file.management :refer :all]
            [cetl.utils.file-utils :refer [file-exists? dir-exists?]]))

(-> {:path "/Users/gra11/Development"
     :file "file.txt"
     :exec :count-row-file}
    (cetl-file-management))


(cetl-copy-file {:file "file.txt"
                 :path ["/Users/gra11/Development" "/Users/gra11"]})


(defn cetl-zip-file
  [x]
  (if (nil? x)
    nil
    (map (fn [path]
           (let [path-obj (io/file path)
                 parent-dir (parent-path path-obj)
                 file-name (file-name path-obj)]
             (do
               (clojure.java.shell/sh
                 "sh" "-c"
                 (str " cd " parent-dir ";" " zip " file-name ".zip" " -r " file-name))
               (assoc x
                 :path [(str parent-dir "/" file-name ".zip")]
                 :exec :cetl-zip-file)))) (path-from-map x))))