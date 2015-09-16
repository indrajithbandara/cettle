(ns cetl.process.components-process
  (:require [clojure.core.async :refer [chan <!! >!! >! <! put! take! close!
                                        sliding-buffer
                                        dropping-buffer
                                        thread go]]
            [cetl.file.management :as fm]))

;TODO use a macro to create a process-> function that takes a variable amount of functions and processes them sequentially

(defmacro process->
  "Macro that process a sequence of
  ETL functions each on a seperate
  thread"
  [& body]
  `(.start
     (Thread.
       (fn [] ~@body))))


(let [c (chan)]
  (thread (>!! c (fm/cetl-file-archive
                   "/Users/gregadebesin/Development/Cetl/archive"
                   :archive-format :gzip))
          (>!! c (fm/cetl-file-archive
                   "/Users/gregadebesin/Development/Cetl/archive"
                   :archive-format :zip)))
  (thread (dotimes [x 2]
            (<!! c))))