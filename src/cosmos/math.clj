;;; Some basic mathematical operations
(ns cosmos.math)

;; Pi constant
(def PI 3.141592653589793)

;; Clamps a value to the normalized range from 0.0 to 1.0
(defn clamp-normal-value [scalar] (max 0.0 (min scalar 1.0)))

;; Converts degrees to radians
(defn deg-to-rad [deg] (* deg (/ PI 180)))

;; Squares a given number
(defn square [x] (* x x))

;; Because Math/abs is HORRIBLY slow.
;; Using Math/abs would quadruple the execution time of this program.
(defn abs [x] (if (< x 0) (* -1 x) x))