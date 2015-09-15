(ns cetl.process.components-process
  (:require [clojure.core.async :refer [chan <!! >!! >! <! put! take! close!
                                        sliding-buffer
                                        dropping-buffer
                                        thread go]]
            [cetl.file.management :as fm]))


(let [c (chan)]
  (thread (>!! c (cetl-file-archive
                   "/Users/gregadebesin/Development/Cetl/archive"
                   :archive-format :gzip))
          (>!! c (cetl-file-archive
                   "/Users/gregadebesin/Development/Cetl/archive"
                   :archive-format :zip)))
  (thread (dotimes [x 2]
            (<!! c))))