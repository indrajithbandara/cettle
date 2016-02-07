(ns cetl.core-test
  (:require [clojure.test :refer :all]
            [cetl.core :refer :all]
            [clojure.java.io :as io]
            [cetl.file.management :refer :all]
            [cetl.utils.file-utils :refer [file-exists? dir-exists?]]))


(transduce
  (exec-command->map
    (:file-dir command->map)) conj '() ["/Users/gra11/Dropbox" "/Users/gra11/Documents"])


