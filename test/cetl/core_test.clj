(ns cetl.core-test
  (:require [clojure.test :refer :all]
            [cetl.core :refer :all]
            [cetl.file.management :refer :all])
  (:import (java.io File)))

(deftest a-test
  (testing "Components test"
    (is (= 0 1))))


(cetl-file-archive  "/Users/gregadebesin/Development/Cetl/TestFiles"
                    :archive-format :gzip)
; Get file names
(map #(.getName %)
     (file-seq
       (File. "/Users/gregadebesin/Development/Cetl")))

;Get full path names
(filter
  #(if (not (.isDirectory %)) (.getPath %))
     (file-seq
      (File. "/Users/gregadebesin/Development/Cetl")))



(map #(.getPath %) (filter #(if (.isDirectory %) (.getPath %)) (file-seq (File. "/Users/gregadebesin/Development/cetl"))))

; find . `pwd` -maxdepth 1 -not -type dl