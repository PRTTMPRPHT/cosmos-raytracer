;;; Module containing the actual raytracing algorithm.
(ns cosmos.algorithm
  (:require [cosmos.math :refer :all])
  (:require [cosmos.vector3 :refer :all])
  (:require [cosmos.ray :refer :all])
  (:require [cosmos.scene_config :refer :all]))

;; Determines the closest intersection of a ray with any scene object.
(defn find-hit-object [ray, maxDistance]
  ; Collapse all scene objects into a result of the one with the closest intersection
  (reduce (fn [previousResult, currentObject]
            (let [intersectionFunction (get currentObject :intersectionFunction)
                  intersection (intersectionFunction ray currentObject)
                  happened (get intersection :happened)]
              (if happened
                (let [hitLocation (get intersection :hitLocation)
                      originCoord (get ray :originCoord)
                      distance (vec-length-squared (vec-sub hitLocation originCoord))
                      currentDistance (get previousResult :dist)]
                  (if (and (> distance (square MINIMAL_DISTANCE)) (< distance currentDistance))
                    (conj intersection {:dist distance})
                    previousResult))
                previousResult))) {:happened false :dist (square maxDistance)} SCENE_OBJECTS))

;; Calculates ambient light from a material.
(defn calculate-ambient-component [material] (vec-mult-scal (get material :colorAmbient) (get material :intensityAmbient)))

;; Calculates the diffuse component for a hitpoint, light, soft-light point 3-way tuple.
(defn calculate-diffuse-component [light, hitPoint, surfaceNormal, falloff, material]
  (let [lightPosition (get light :position)
        lightColor (get light :color)
        colorDiffuse (get material :colorDiffuse)
        intensityDiffuse (get material :intensityDiffuse)
        diffusion (vec-dot surfaceNormal (vec-normalize (vec-sub lightPosition hitPoint)))]
    (if (> diffusion 0)
      (->
       (vec-mult lightColor colorDiffuse)
       (vec-mult-scal diffusion)
       (vec-mult-scal intensityDiffuse)
       (vec-mult-scal falloff))
      (make-vec 0 0 0))))

;; Calculates the specular component for a hitpoint, light, soft-light point 3-way tuple.
(defn calculate-specular-component [light, hitPoint, reflectedVec, falloff, material]
  (let [lightPosition (get light :position)
        lightColor (get light :color)
        colorSpecular (get material :colorSpecular)
        intensitySpecular (get material :intensitySpecular)
        shininess (get material :shininess)
        specularity (vec-dot reflectedVec (vec-normalize (vec-sub lightPosition hitPoint)))]
    (if (> specularity 0)
      (->
       (vec-mult lightColor colorSpecular)
       (vec-mult-scal (Math/pow specularity shininess))
       (vec-mult-scal intensitySpecular)
       (vec-mult-scal falloff))
      (make-vec 0 0 0))))

;; Calculates specular and diffuse light for a point light and hitpoint pair.
(defn calculate-diffuse-and-specular-for-light [light, intersection, reflectedVec]
  (reduce (fn [prevResult, softLightVector]
            (let [lightPosition (get light :position)
                  hitPoint (get intersection :hitLocation)
			; The soft light vectors make the light actually consist of a "cloud of points"
			; which decreases sharpness and can also shine around edges.
                  vecToRandomLightPoint (vec-sub (vec-add lightPosition softLightVector) hitPoint)
                  distanceToLight (vec-length vecToRandomLightPoint)
			; Cast new ray to light to determine if there is any obstruction to the light
                  lightDirection (vec-normalize vecToRandomLightPoint)
                  lightRayOrigin (vec-add hitPoint (vec-mult-scal lightDirection MINIMAL_DISTANCE))
                  rayToLight (make-ray lightRayOrigin lightDirection)
                  shadowDistance (- distanceToLight MINIMAL_DISTANCE)
			; Actual obstruction detection
                  lightHitResult (find-hit-object rayToLight shadowDistance)]
              (if (get lightHitResult :happened)
                prevResult ; Obstructed. No light modification happens
                (let [baseFalloff (vec-length-squared (vec-sub hitPoint lightPosition))
                      falloff (/ 1 baseFalloff)
                      material (get intersection :material)
                      surfaceNormal (get intersection :surfaceNormal)]
                  {:diffuse (calculate-diffuse-component light, hitPoint, surfaceNormal, falloff, material)
                   :specular (calculate-specular-component light, hitPoint, reflectedVec, falloff, material)})))) {:specular (make-vec 0 0 0) :diffuse (make-vec 0 0 0)} SOFT_LIGHT_VECTORS))

;; Calculates diffuse and specular component for a intersection.
;; These are both grouped together for performance as we have to loop both over the
;; point lights AND the soft light vectors for both components. This saves time.
(defn calculate-diffuse-and-specular-component [intersection, reflectedVec]
  (reduce (fn [prevResult, currentLight]
            (let [prevSpecular (get prevResult :specular)
                  prevDiffuse (get prevResult :diffuse)
                  diffuseAndSpecular (calculate-diffuse-and-specular-for-light currentLight intersection reflectedVec)
                  newSpecular (get diffuseAndSpecular :specular)
                  newDiffuse (get diffuseAndSpecular :diffuse)]
              {:specular (vec-add prevSpecular newSpecular) :diffuse (vec-add prevDiffuse newDiffuse)})) {:specular (make-vec 0 0 0) :diffuse (make-vec 0 0 0)} SCENE_LIGHTS))

;; Declare ray-trace function because there is a circular dependecy between calculate-reflection-component and ray-trace.
(declare ray-trace)

;; Calculates the reflection component of a given hitpoint.
(defn calculate-reflection-component [ray, iterationsLeft, intersection, reflectedVec]
  (if (<= iterationsLeft 1)
    (make-vec 0 0 0)
    (let [surfaceNormal (get intersection :surfaceNormal)
          hitLocation (get intersection :hitLocation)
          newRayOrigin (vec-add hitLocation (vec-mult-scal reflectedVec MINIMAL_DISTANCE))
          newRay (make-ray newRayOrigin, reflectedVec)
          intensityReflection (get (get intersection :material) :intensityReflection)]
      (vec-mult-scal (ray-trace newRay (dec iterationsLeft)) intensityReflection))))

;; Performs the entire raytracing pass for a given ray, consisting of:
;; - Ambient Light
;; - Diffuse Light
;; - Specular Light
;; - Reflected Light
(defn ray-trace [ray, iterationsLeft]
  (let [intersection (find-hit-object ray MAXIMAL_DISTANCE)
        intersectionHappened (get intersection :happened)]
    (if intersectionHappened
      (let [material (get intersection :material)
            surfaceNormal (get intersection :surfaceNormal)
            rayDirection (get ray :direction)
            reflectedVec (vec-mirror-on-axis (vec-mult-scal rayDirection -1) surfaceNormal)
            ambientComponent (calculate-ambient-component material)
            reflectionComponent (calculate-reflection-component ray iterationsLeft intersection reflectedVec)
            diffuseAndSpecularComponent (calculate-diffuse-and-specular-component intersection reflectedVec)]
        (->>
         (vec-add ambientComponent reflectionComponent)
         (vec-add (get diffuseAndSpecularComponent :diffuse))
         (vec-add (get diffuseAndSpecularComponent :specular))))
      (make-vec 0 0 0))))