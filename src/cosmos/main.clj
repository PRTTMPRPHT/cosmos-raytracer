;;; Main module that is executed when starting the program.
(ns cosmos.main
  (:import [javax.imageio ImageIO])
  (:import [java.io File])
  (:require [cosmos.math :refer :all])
  (:require [cosmos.vector3 :refer :all])
  (:require [cosmos.ray :refer :all])
  (:require [cosmos.matrix :refer :all])
  (:require [cosmos.image_util :refer :all])
  (:require [cosmos.scene_object :refer :all])
  (:require [cosmos.scene_config :refer :all])
  (:require [cosmos.algorithm :refer :all])
  (:gen-class))

;; Marks the start of a ray tracing sequence for one pixel and clamps the result to
;; a normalized range of 0.0-1.0 for further processing.
(defn ray-trace-begin [ray] (vec-clamp (ray-trace ray MAX_ITERATIONS)))

;; Constructs a lazy sequence of rays. Since this is a process that consists of multiple
;; steps going from a scalar value to a ray consisting of two vectors, it's split up into
;; multiple, simple steps.
(defn get-ray-sequence [width, height]
  (->>
   ; Enumerate pixels
   (range 0 (* width height)) 
   ; Map pixel index to X/Y coordinate pair
   (map #(vector (get-pixel-x % width) (get-pixel-y % width)))
   ; Create initial directional vectors from pixel coordinates
   (map #(let [pixX (get % 0)
               pixY (get % 1)]
           (make-vec (* (- (/ pixX width) 0.5) ASPECT_RATIO) (- (/ pixY height) 0.5) CAMERA_ZOOM_FACTOR)))
   ; Normalize them into unit vectors
   (map #(vec-normalize %))
   ; Rotate them according to the pre-generated rotation matrix
   (map #(mat-rotate % CAMERA_ROTATION_MATRIX))
   ; Cast a ray from the camera position
   (map #(make-ray CAMERA_POSITION %))))

;; Applies the ray tracing algorithm to a generated sequence of rays.
;; This is done in parallel since pixels can be calculated independently of each other.
(defn apply-ray-trace [width, height]
  (pmap ray-trace-begin (get-ray-sequence width height)))

;; Performs the entire rendering pass for one picture.
(defn perform-rendering []
  ; First, instantiate the image
  (let [frameBuffer (instantiate-frame-buffer IMAGE_WIDTH IMAGE_HEIGHT)]
    ; Apply ray tracing algorithm to each pixel
    (doseq [[index color] (map-indexed vector (apply-ray-trace IMAGE_WIDTH IMAGE_HEIGHT))]
      ; Write operation for single pixel
      (put-pixel frameBuffer index IMAGE_WIDTH color)
      ; Debug output in regular intervals
      (if (= (mod index 100) 0)
        (println (str "Putting index " index " of " (* IMAGE_WIDTH IMAGE_HEIGHT) " (" (double (* 100 (/ index (* IMAGE_WIDTH IMAGE_HEIGHT)))) "%) - " color))))
    (ImageIO/write frameBuffer "png" (File. FILE_OUTPUT_PATH))
    (println "Finished rendering!")))

;; Performs the rendering pass for one picture and measures the time it took.
(defn render-with-performance-measurement []
  (let [startTime (System/currentTimeMillis)]
    (perform-rendering)
    (println (str "Finished after " (- (System/currentTimeMillis) startTime) "ms."))))

; Execute!
(render-with-performance-measurement)

; Terminate pmap threads afterwards because clojure doesn't wanna do it on its own.
(println "Shutting down pmap threads.")
(shutdown-agents) 