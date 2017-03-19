(ns cettle.clojure.core
  (:import [cettle.java WeightedDistanceMatrix Vertex]
           (java.util ArrayList Arrays)))


(def a (Vertex. "A"))
(def b (Vertex. "B"))
(def c (Vertex. "C"))
(def d (Vertex. "D"))

(def from (ArrayList. [a c]))
(def to (ArrayList. [b d]))
(def weight (ArrayList. [22.2 44.4]))


(defn wdmatrix [directed? ^double non-edge ^long verts]
  (WeightedDistanceMatrix/build from to weight directed? non-edge verts))


(WeightedDistanceMatrix/build from to weight false 0.0 4)
  "A" "B" "C" "D"

[[0.0 0.0 44.4 0.0] [0.0 0.0 0.0 22.2] [44.4 0.0 0.0 0.0] [0.0 22.2 0.0 0.0]]