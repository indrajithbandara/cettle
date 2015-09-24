(ns cetl.core-test
  (:require [clojure.test :refer :all]
            [cetl.core :refer :all]
            [cetl.file.management :refer :all])
  (:import (java.io File)
           (org.apache.commons.io FileUtils)))

(deftest a-test
  (testing "Components test"
    (is (= 0 1))))


;TODO test why "/Users/gregadebesin/Development/Cetl/TestFiles copy" a path with a space in name dosent work

(def list-files (cetl-file-list {:path "/Users/gregadebesin/Development/Cetl"
                                 :list :dirs}))

(map (fn [x] (cetl-file-archive {:path x :archive :zip})) list-files)

; file -I test-file.txt
