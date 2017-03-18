(ns cettle.clojure.core
  (:import [cettle.java WeightedDistanceMatrix Vertex]
           (java.util ArrayList Arrays)))

(defn wdmatrix [^long verts directed? ^double non-edge]
  (WeightedDistanceMatrix. verts directed? non-edge))

(def a (Vertex. "A"))
(def b (Vertex. "B"))
(def c (Vertex. "C"))
(def d (Vertex. "D"))

(def from (ArrayList. [a c]))
(def to (ArrayList. [b d]))
(def weight (ArrayList. [22.2 44.4]))

(WeightedDistanceMatrix/build from to weight false 0.0 4)

