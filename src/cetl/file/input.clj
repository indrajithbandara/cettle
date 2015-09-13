(ns cetl.file.input
  (:import (au.com.bytecode.opencsv CSVReader)))
(use 'incanter.io)

(defn cetl-input-file-delimited
  [filename & {:keys [delim keyword-headers quote skip header compress-delim empty-field-value]
               :or {delim \, quote \" skip 0 header false keyword-headers true}}]
  (letfn [(parse-string [value & [empty-value]]
                        (if (= value "")
                          empty-value
                          (if (re-matches #"\d+" value)
                            (try (Long/parseLong value)
                                 (catch NumberFormatException _ value))
                            (try (Double/parseDouble value)
                                 (catch NumberFormatException _ value)))))
          (pad-vector [v new-len value]
                      (into v (repeat (- new-len (count v)) value)))]
    (let [compress-delim? (or compress-delim (= delim \space))
          compress-delim-fn (if compress-delim?
                              (fn [line] (filter #(not= % "") line))
                              identity)
          remove-empty-fn #(when (some (fn [field] (not= field "")) %) %)
          parse-data-fn (fn [line]
                          (vec (map #(parse-string % empty-field-value) line)))
          [parsed-data column-count]
          (with-open [reader ^CSVReader (CSVReader. (clojure.java.io/reader filename) delim quote skip)]
            (loop [lines [] max-column 0]
              (if-let [line (.readNext reader)]
                (let [new-line (-> line
                                   compress-delim-fn
                                   remove-empty-fn
                                   parse-data-fn)]
                  (recur (if-not (empty? new-line) (conj lines new-line) lines)
                         (max max-column (count new-line))))
                [lines max-column])))
          header-row (when header (first parsed-data))
          dataset-body (if header (rest parsed-data) parsed-data)
          column-names-strs
          (map (fn [hr-entry idx]
                 (or hr-entry (str "col" idx)))
               (concat header-row (repeat nil))
               (range column-count))
          column-names (map (if keyword-headers keyword identity) column-names-strs)
          padded-body
          (if (not (nil? empty-field-value))
            (map #(pad-vector % column-count empty-field-value)
                 dataset-body)
            dataset-body)]
      (incanter.core/dataset column-names padded-body))))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

