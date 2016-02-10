(ns cetl.shell.sh
  (:use [clojure.java.io :only (as-file copy)])
  (:import (java.io ByteArrayOutputStream StringWriter)
           (java.nio.charset Charset)))


(def ^:dynamic *sh-dir* nil)
(def ^:dynamic *sh-env* nil)

(defn- stream-to-bytes
  [in]
  (with-open [bout (ByteArrayOutputStream.)]
    (copy in bout)
    (.toByteArray bout)))

(defn- stream-to-string
  ([in] (stream-to-string in (.name (Charset/defaultCharset))))
  ([in enc]
   (with-open [bout (StringWriter.)]
     (copy in bout :encoding enc)
     (.toString bout))))

(defn- stream-to-enc
  [stream enc]
  (if (= enc :bytes)
    (stream-to-bytes stream)
    (stream-to-string stream enc)))

(defn- parse-args
  [args]
  (let [default-encoding "UTF-8" ;; see sh doc string
        default-opts {:out-enc default-encoding :in-enc default-encoding :dir *sh-dir* :env *sh-env*}
        [cmd opts] (split-with string? args)]
    [cmd (merge default-opts (apply hash-map opts))]))

(defn- ^"[Ljava.lang.String;" as-env-strings
  "Helper so that callers can pass a Clojure map for the :env to sh."
  [arg]
  (cond
    (nil? arg) nil
    (map? arg) (into-array String (map (fn [[k v]] (str (name k) "=" v)) arg))
    true arg))

(defn exec
  {:added "1.2"}
  [& args]
  (let [[cmd opts] (parse-args args)
        proc (.exec (Runtime/getRuntime)
                    ^"[Ljava.lang.String;" (into-array cmd)
                    (as-env-strings (:env opts))
                    (as-file (:dir opts)))
        {:keys [in in-enc out-enc]} opts]
    (if in
      (future
        (with-open [os (.getOutputStream proc)]
          (copy in os :encoding in-enc)))
      (.close (.getOutputStream proc)))
    (with-open [stdout (.getInputStream proc)
                stderr (.getErrorStream proc)]
      (let [out (future (stream-to-enc stdout out-enc))
            err (future (stream-to-string stderr))
            exit-code (.waitFor proc)]
        {:exit exit-code :out @out :err @err}))))

(defn exec-command
  [p]
  (fn [s]
    (clojure.string/split
      (get (exec "sh" "-c" (str " cd " s ";" p)) :out) #"\n")))

(defn exec-commands
  [p]
  (map (fn [s]
         (clojure.string/split
           (get (exec "sh" "-c" (str " cd " s ";" p)) :out) #"\n"))))
