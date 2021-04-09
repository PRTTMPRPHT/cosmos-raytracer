;;; This module describes the structure and behavior of scene objects.
;;; Currently, only point lights, planes and spheres are implemented.
(ns cosmos.scene_object
  (:require [cosmos.math :refer :all])
  (:require [cosmos.vector3 :refer :all])
  (:require [cosmos.ray :refer :all]))

;; Declarations of intersection functions because they are defined later below but used before.
(declare intersect-plane)
(declare intersect-sphere)

;; "Constructor" for a material. These are parameters that can influence how an object is rendered.
(defn make-material [intensityAmbient,
                     intensityDiffuse,
                     intensitySpecular,
                     intensityReflection,
                     colorAmbient,
                     colorDiffuse,
                     colorSpecular,
                     shininess] {:intensityAmbient intensityAmbient
                                 :intensityDiffuse intensityDiffuse
                                 :intensitySpecular intensitySpecular
                                 :intensityReflection intensityReflection
                                 :colorAmbient colorAmbient
                                 :colorDiffuse colorDiffuse
                                 :colorSpecular colorSpecular
                                 :shininess shininess})

;; "Constructor" for a point light.
(defn make-light [positionVec, colorVec] {:position positionVec
                                          :color colorVec})

;; "Constructor" for a plane scene object.
(defn make-plane [locationVec, normalVec, material] {:type "plane"
                                                     :location locationVec
                                                     :normal normalVec
                                                     :material material
                                                     :intersectionFunction intersect-plane})

;; "Constructor" for a sphere scene object.
(defn make-sphere [locationVec, radius, material] {:type "sphere"
                                                   :location locationVec
                                                   :radius radius
                                                   :material material
                                                   :intersectionFunction intersect-sphere})

;; Ray intersection result "constructor".
(defn make-intersection [happened, hitLocation, surfaceNormal, material] {:happened happened
                                                                          :hitLocation hitLocation
                                                                          :surfaceNormal surfaceNormal
                                                                          :material material})

;; Default object if no intersection with an object has happened.
(def FAILED_INTERSECTION (make-intersection false nil nil nil))

;; Intersection function for a plane object.
(defn intersect-plane [ray, plane]
  (let [rayDirection (get ray :direction)
        rayOrigin (get ray :originCoord)
        planeNormal (get plane :normal)
        top (vec-dot (vec-sub (get plane :location) rayOrigin) planeNormal)
        bottom (vec-dot (get ray :direction) planeNormal)]
    (if (< (abs bottom) MINIMAL_DISTANCE)
      FAILED_INTERSECTION
      (let [ratio (/ top bottom)]
        (if (< ratio 0)
          FAILED_INTERSECTION
          (make-intersection true (vec-add rayOrigin (vec-mult-scal rayDirection ratio)) planeNormal (get plane :material)))))))

;; Intersection function for a scene object.
(defn intersect-sphere [ray, sphere]
  (let [rayDirection (get ray :direction)
        rayOrigin (get ray :originCoord)
        sphereLocation (get sphere :location)
        sphereRadius (get sphere :radius)
        vecToSphere (vec-sub rayOrigin sphereLocation)
        rayDot (vec-dot rayDirection vecToSphere)
        directionDot (vec-length-squared rayDirection)
        solution (- (square rayDot) (* directionDot (- (vec-length-squared vecToSphere) (square sphereRadius))))]
    (if (< solution MINIMAL_DISTANCE)
      FAILED_INTERSECTION
      (let [solutionRoot (Math/sqrt solution)
            negativeRayDot (* -1 rayDot)
            dist (/ (min (- negativeRayDot solutionRoot) (+ negativeRayDot solutionRoot)) directionDot)]
        (if (< dist MINIMAL_DISTANCE)
          FAILED_INTERSECTION
          (let [hitLocation (vec-add rayOrigin (vec-mult-scal rayDirection dist))
                normal (vec-div-scal (vec-sub hitLocation sphereLocation) sphereRadius)]
            (make-intersection true hitLocation normal (get sphere :material))))))))