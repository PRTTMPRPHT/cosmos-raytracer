;;; Module related to basic vector math.
;;; Mathematical vectors are represented here as Clojure-vectors with three values.
;;; These are used both for calculations pertaining to raytracing and colors.
(ns cosmos.vector3
  (:require [cosmos.math :refer :all]))

;; Fixed "constructor" for the three-dimensional vectors.
(defn make-vec [x, y, z] (vector x y z))

;; Some accessor constants to abstract away from Clojure vectors.
(def VEC_X 0)
(def VEC_Y 1)
(def VEC_Z 2)
(def RGB_R VEC_X)
(def RGB_G VEC_Y)
(def RGB_B VEC_Z)
(defn vecX [vec] (get vec VEC_X))
(defn vecY [vec] (get vec VEC_Y))
(defn vecZ [vec] (get vec VEC_Z))
(defn rgbR [rgb] (vecX rgb))
(defn rgbG [rgb] (vecY rgb))
(defn rgbB [rgb] (vecZ rgb))

;; Executes an arbitrary mathematical function on two vectors.
(defn vec-math [operand, vecA, vecB]
  (make-vec
   (operand (vecX vecA) (vecX vecB))
   (operand (vecY vecA) (vecY vecB))
   (operand (vecZ vecA) (vecZ vecB))))

;; Executes an arbitrary mathematical function on a vector and a scalar value.
(defn vec-math-scalar [operand, vec, scalar]
  (make-vec
   (operand (vecX vec) scalar)
   (operand (vecY vec) scalar)
   (operand (vecZ vec) scalar)))

;; Implementation of standard mathematical operations.
(defn vec-add [vecA, vecB] (vec-math + vecA vecB))
(defn vec-mult [vecA, vecB] (vec-math * vecA vecB))
(defn vec-div [vecA, vecB] (vec-math / vecA vecB))
(defn vec-sub [vecA, vecB] (vec-math - vecA vecB))
(defn vec-add-scal [vecA, scal] (vec-math-scalar + vecA scal))
(defn vec-mult-scal [vecA, scal] (vec-math-scalar * vecA scal))
(defn vec-div-scal [vecA, scal] (vec-math-scalar / vecA scal))
(defn vec-sub-scal [vecA, scal] (vec-math-scalar - vecA scal))

;; Ensures boundaries of 0.0-1.0 per value.
(defn vec-clamp [vec]
  (make-vec
   (clamp-normal-value (vecX vec))
   (clamp-normal-value (vecY vec))
   (clamp-normal-value (vecZ vec))))

;; Calculates the dot product of two vectors.
(defn vec-dot [vecA vecB]
  (+
   (* (vecX vecA) (vecX vecB))
   (* (vecY vecA) (vecY vecB))
   (* (vecZ vecA) (vecZ vecB))))

;; Length calculations.
(defn vec-length-squared [vec] (vec-dot vec vec))
(defn vec-length [vec] (Math/sqrt (vec-length-squared vec)))

;; Creates a unit vector.
(defn vec-normalize [vec]
  (let [length (vec-length vec)]
    (vec-div-scal vec length)))

;; Mirrors a vector on an axis.
(defn vec-mirror-on-axis [vec, axisVec]
  (let [axisUnit (vec-normalize axisVec)
        projection (vec-mult-scal axisUnit (vec-dot vec axisUnit))
        step (vec-sub projection, vec)]
    (vec-add vec (vec-mult-scal step 2))))

;; Converts an euler angle in degrees to radians.
(defn vec-deg-to-rad [angleVec]
  (make-vec
   (deg-to-rad (vecX angleVec))
   (deg-to-rad (vecY angleVec))
   (deg-to-rad (vecZ angleVec))))

;; Reduces a normalized (0.0-1.0) RGB vector to a single value that can be used for rendering.
(defn rgb-to-int [rgbVec]
  (let [clamped (vec-clamp rgbVec)
        red (bit-shift-left (bit-and 0xFF (int (* 255 (rgbR clamped)))) 16)
        green (bit-shift-left (bit-and 0xFF (int (* 255 (rgbG clamped)))) 8)
        blue (bit-and 0xFF (int (* 255 (rgbB clamped))))]
    (bit-or red green blue)))