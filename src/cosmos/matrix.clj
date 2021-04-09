;;; For rotation of vectors, 3x3 matrices consisting of a vector of three Vector3s are used.
(ns cosmos.matrix
  (:require [cosmos.vector3 :refer :all]))

;; Accessors.
(defn m1 [mat] (get mat 0))
(defn m2 [mat] (get mat 1))
(defn m3 [mat] (get mat 2))

;; Creates a matrix from an euler angle.
(defn make-rotation-matrix [angleInDegVec]
  (let [angleVec (vec-deg-to-rad angleInDegVec)
        x (vecX angleVec)
        y (vecY angleVec)
        z (vecZ angleVec)
        sx (Math/sin x)
        sy (Math/sin y)
        sz (Math/sin z)
        cx (Math/cos x)
        cy (Math/cos y)
        cz (Math/cos z)]
    (vector
     (make-vec
      (* cy cz)
      (* cy sz)
      (* -1 sy))
     (make-vec
      (- (* sx cz sy) (* cx sz))
      (+ (* sx sz sy) (* cx cz))
      (* sx cy))
     (make-vec
      (+ (* cx cz sy) (* sx sz))
      (- (* cx sz sy) (* sx cz))
      (* cx cy)))))

;; Rotates a vector using a rotation matrix.
(defn mat-rotate [vec, mat]
  (make-vec
   (vec-dot (m1 mat) vec)
   (vec-dot (m2 mat) vec)
   (vec-dot (m3 mat) vec)))