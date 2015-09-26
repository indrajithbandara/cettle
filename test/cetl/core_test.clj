(ns cetl.core-test
  (:require [clojure.test :refer :all]
            [cetl.core :refer :all]
            [cetl.file.management :refer :all]))

(deftest a-test
  (testing "Components test"
    (is (map? (cetl-file-archive )))))


;TODO test why "/Users/gregadebesin/Development/Cetl/TestFiles copy" a path with a space in name dosent work

(def list-files (cetl-file-list {:path "/Users/gregadebesin/Development/Cetl"
                                 :list :dirs}))

(map (fn [x] (cetl-file-archive {:path x :archive :zip})) list-files)

; file -I test-file.txt
