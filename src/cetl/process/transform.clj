(ns cetl.process.transform
  (import (java.util ArrayList)
          (org.josql Query)))


(def animals (ArrayList. ["Cat" "Dog" "Sheep"]))

(def query (Query.))


(def parser (.parse query "select * from java.lang.String where length=3"))

(def results (.getResults (.execute query animals)))

