(ns cetl.file.output)

(defn cetl-output-file-delimited
  [dataset filename & {:keys [delim header append]
                       :or {append false delim \,}}]
  (let [header (or header (map #(if (keyword? %) (name %) %) (:column-names dataset)))
        file-writer (if (= "-" filename)
                      *out*
                      (java.io.FileWriter. filename append))
        rows (:rows dataset)
        columns (:column-names dataset)]
    (try
      (when (and header (not append))
        (.write file-writer (str (first header)))
        (doseq [column-name (rest header)]
          (.write file-writer (str delim column-name)))
        (.write file-writer (str \newline)))
      (doseq [row rows]
        (do
          (.write file-writer (str (row (first columns))))
          (doseq [column-name (rest columns)]
            (.write file-writer (str delim (row column-name))))
          (.write file-writer (str \newline))))
      (finally
        (.flush file-writer)
        (when (= "-" filename)
          (.close file-writer))))))