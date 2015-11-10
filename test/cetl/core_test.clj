(ns cetl.core-test
  (:require [clojure.test :refer :all]
            [cetl.core :refer :all]
            [cetl.file.management :refer :all]))



(cetl-list-file {:path "/Users/gregadebesin/Development"
                 :exec :list-fils})

(cetl-list-file {:path "/Users/gregadebesin/Developmen"
                 :exec :list-dirs-files})

(cetl-list-file {:path "/Users/gregadebesin/Development"
                 :exec :list-dirs-sub-dirs})

(cetl-list-file {:path "/Users/gregadebesin/Development"
                 :exec :list-dirs})

;=====================================================================

(cetl-archive-file {:path "/Users/gregadebesin/Development/Cetl"
                    :file "archive"
                    :exec :zip-file})

(cetl-archive-file {:path "/Users/gregadebesin/Development/Cetl"
                    :file "archive"
                    :exec :gzip-file})

(cetl-create-temp-file {:path "/Users/gregadebesin/Development"
                        :file "foo.tmp"
                        :exec :create-temp-file})

(cetl-copy-file {:in-path "/Users/gregadebesin/Development"
                 :out-path "/Users/gregadebesin"
                 :file "foo.tmp"
                 :exec :copy-file})

(cetl-delete-file {:path "/Users/gregadebesin/Development"
                   :file "foo.tmp"
                   :exec :delete-file})

(cetl-file-properties {:path "/Users/gregadebesin/Development/datomic-code.txt"
                       :exec :file-properties})





