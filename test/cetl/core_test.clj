(ns cetl.core-test
  (:require [clojure.test :refer :all]
            [cetl.core :refer :all]
            [cetl.file.management :refer :all]))

(cetl-file-management {:path "/Users/gregadebesin/Development/Cetl"
                    :file "foo"
                    :exec :zip-file})

(cetl-file-management {:path "/Users/gregadebesin/Development/Cetl"
                    :file "foo"
                    :exec :gzip-file})

(cetl-file-management {:path "/Users/gregadebesin/Development"
                        :file "foo.tmp"
                        :exec :create-temp-file})

(cetl-file-management {:in-path "/Users/gregadebesin/Development"
                 :out-path "/Users/gregadebesin"
                 :file "foo.tmp"
                 :exec :copy-file})

(cetl-file-management {:path "/Users/gregadebesin/Development"
                   :file "untitled.txt"
                   :exec :delete-file})

(cetl-file-management {:path "/Users/gregadebesin/Development"
                       :file "foo.tmp"
                       :exec :properties-file})

(cetl-file-management {:path "/Users/gregadebesin/Development/untitled.txt"
                   :exec :ISO-8859-1})


(cetl-file-management {:file-one "foo.txt"
                    :path-one "/Users/gregadebesin/Development"
                    :file-two "bar.txt"
                    :path-two "/Users/gregadebesin/Development"
                    :exec :compare-file})

(cetl-file-management {:file "tennis.txt"
                      :path "/Users/gregadebesin/Development"
                      :exec :count-row-file})

(cetl-file-management {:file "foo.txt"
                  :path "/Users/gregadebesin/Development"
                  :exec :touch-file})
  
  (cetl-file-management {:file "bar.txt"
                        :path "/Users/gregadeesin/Development"
                        :recipient "Gregory"
                        :exec :gpg-encrypt-file})
