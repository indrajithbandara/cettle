(ns cetl.file.management
  (:require [clojure.string :as s])
  (:import (org.apache.commons.io FileUtils)
           (java.io File)))

(use '[clojure.java.shell :only [sh]])


;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defn cetl-file-list
  [path & {:keys
           [dirs-only? files-only? dirs-&-files?
            include-sub-dirs?]}]
  (letfn
    [(dirs-&-files
       [p]
       (clojure.string/split
         (get (clojure.java.shell/sh
                "sh" "-c"
                (str " cd " p ";"
                     " find `pwd` -maxdepth 1 "))
              :out) #"\n"))
     (files-only
       [p]
       (clojure.string/split
         (get (clojure.java.shell/sh
                "sh" "-c"
                (str " cd " p ";"
                     " find `pwd` -type f -maxdepth 1 "))
              :out) #"\n"))
     (dirs-only
       [p]
       (clojure.string/split
         (get (clojure.java.shell/sh
                "sh" "-c"
                (str " cd " p ";"
                     " find `pwd` -type d -maxdepth 1 "))
              :out) #"\n"))
     (include-dirs-only-sub-dirs
       [p]
       (clojure.string/split
         (get (clojure.java.shell/sh
                "sh" "-c"
                (str " cd " p ";"
                     " find `pwd` -type d "))
              :out) #"\n"))]
    (cond
      (and (= dirs-&-files? true)
           (= include-sub-dirs? false))
      (dirs-&-files path)
      (and (= files-only? true)
           (= include-sub-dirs? false))
      (files-only path)
      (and (= dirs-only? true)
           (= include-sub-dirs? false))
      (dirs-only path)
      (and (= dirs-only? true)
           (= include-sub-dirs? true))
      (include-dirs-only-sub-dirs path))))



#_(defn cetl-file-unarchive
    [path in out & {:keys []}])

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defn cetl-file-archive
  [path & {:keys [archive-format]}]
  (letfn
    [(zip
       [p]
       (clojure.java.shell/sh
         "sh" "-c"
         (str " cd " (.getParent (File. p)) "/;"
              " zip " (last (s/split p #"/")) ".zip"
              " -r " (last (s/split p #"/")))))
     (gzip
       [p]
       (clojure.java.shell/sh
         "sh" "-c"
         (str " cd " (.getParent (File. p)) "/;"
              " tar -cvzf " (last (s/split p #"/")) ".tar.gz"
              " " (last (s/split p #"/")))))]
    (cond
      (= archive-format :zip) (zip path)
      (= archive-format :gzip) (gzip path))))
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defn cetl-change-file-encoding
  [in-path out-path & {:keys [from-encode to-encode]}]
  (letfn [(encoding
            [in out x xs]
            (FileUtils/write
              (File. out)
              (FileUtils/readFileToString
                (File. in) x) xs))]
    (cond (and (= from-encode :UTF-8)
               (= to-encode :ISO-8859-15))
          (encoding in-path out-path "UTF-8" "ISO-8859-15")
          (and (= from-encode :ISO-8859-15)
               (= to-encode :UTF-8))
          (encoding in-path out-path "ISO-8859-15" "UTF-8"))))


;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;



(defmulti cetl-file-list (fn [x] (:list x)))

(defmethod cetl-file-list :dirs-and-files?
  []
  )

(defmethod cetl-file-list :files?
  []
  )

(defmethod cetl-file-list :dirs?
  []
  )

(defmethod cetl-file-list :dirs-only-sub-dirs?
  []
  )

(defmethod cetl-file-list :files-only-sub-dirs?
  []
  )




(defmulti full-moon-behavior (fn [were-creature] (:were-type were-creature)))

(defmethod full-moon-behavior :wolf
  [were-creature]
  (+ 8 8))

(defmethod full-moon-behavior :simmons
  [were-creature]
  (str (:name were-creature) " will encourage people and sweat to the oldies"))

(full-moon-behavior {:were-type :wolf})
; => "Rachel from next door will howl and murder"

(full-moon-behavior {:name "Andy the baker"
                     :were-type :simmons})
; => "Andy the baker will encourage people an












