(ns cettle.clojure.core
  (:import cettle.java.graph.WeightedDistanceMatrix))


(defn wdmatrix [^long verts directed? ^double non-edge]
  (WeightedDistanceMatrix. verts directed? non-edge))