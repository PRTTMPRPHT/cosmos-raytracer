;;; Module dealing with the concepts of "rays", that have an origin
;;; and a direction.
(ns cosmos.ray
  (:require [cosmos.vector3 :refer :all]))

;; "Constructor" for a ray.
(defn make-ray [originCoord directionVec] {:originCoord originCoord
                                           :direction directionVec})

;; Constants related to raycasting.
(def MINIMAL_DISTANCE 0.0001)
(def MAXIMAL_DISTANCE 100000)