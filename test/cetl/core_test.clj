(ns cetl.core-test
  (:require [clojure.test :refer :all]
            [cetl.core :refer :all]
            [clojure.java.io :as io]
            [cetl.file.terminal :refer :all]
            [cetl.utils.file-utils :refer [file-exists? dir-exists?]]))


((cetl-compress (compress-command "file.txt" "data-dump/" "filecopy.txt"))
  :zip-exc-file ["/Users/gra11/Development"])