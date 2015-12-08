(ns cetl.core-test
  (:require [clojure.test :refer :all]
            [cetl.core :refer :all]
            [clojure.java.io :as io]
            [cetl.file.management :refer :all]
            [cetl.utils.component-utils :refer [file-exists? dir-exists?]]))

(-> {:path "/Users/gra11/Development"
     :file "file.txt"
     :exec :count-row-file}
    (cetl-file-management))

(-> {:path "/Users/gra11/Development"
     :file "file.txt"}
    (->file-management)
    (zip-file)
    (->file-management)
    (gzip-file))


(cetl-copy-file {:file "file.txt"
                 :path ["/Users/gra11/Development" "/Users/gra11"]})