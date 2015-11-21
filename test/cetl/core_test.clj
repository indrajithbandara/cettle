(ns cetl.core-test
  (:require [clojure.test :refer :all]
            [cetl.core :refer :all]
            [cetl.file.management :refer :all]))



(cetl-list-file {:path "/Users/gregadebesin/Development"
                 :exec :list-files})

(cetl-list-file {:path "/Users/gregadebesin/Development"
                 :exec :list-dirs-files})

(cetl-list-file {:path "/Users/gregadebesin/Development"
                 :exec :list-dirs-sub-dirs})

(cetl-list-file {:path "/Users/gregadebesin/Development"
                 :exec :list-dirs})

;=====================================================================

(cetl-archive-file {:path "/Users/gregadebesin/Development/Cetl"
                    :file "foo"
                    :exec :zip-file})

(cetl-archive-file {:path "/Users/gregadebesin/Development/Cetl"
                    :file "foo"
                    :exec :gzip-file})

(cetl-create-temp-file {:path "/Users/gregadebesin/Development"
                        :file "foo.tmp"
                        :exec :create-temp-file})

(cetl-copy-file {:in-path "/Users/gregadebesin/Development"
                 :out-path "/Users/gregadebesin"
                 :file "foo.tmp"
                 :exec :copy-file})

(cetl-delete-file {:path "/Users/gregadebesin/Development"
                   :file "untitled.txt"
                   :exec :delete-file})

(cetl-properties-file {:path "/Users/gregadebesin/Development"
                       :file "foo.tmp"
                       :exec :file-properties})

(cetl-encode-file {:path "/Users/gregadebesin/Development/untitled.txt"
                   :exec :ISO-8859-1})


(cetl-compare-file {:file-one "foo.txt"
                    :path-one "/Users/gregadebesin/Development"
                    :file-two "bar.txt"
                    :path-two "/Users/gregadebesin/Development"
                    :exec :compare-file})