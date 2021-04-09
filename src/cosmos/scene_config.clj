;;; Configuration and constants for the entire scenes.
;;; All scene objects are created here and all necessary parameters are set.
;;; This can be easily edited if the scene should be changed.
(ns cosmos.scene_config
  (:require [cosmos.vector3 :refer :all])
  (:require [cosmos.matrix :refer :all])
  (:require [cosmos.scene_object :refer :all]))

;; Where to save the computed image.
(def FILE_OUTPUT_PATH "./cosmos-output.png")

;; Image properties.
(def IMAGE_WIDTH 640)
(def IMAGE_HEIGHT 480)
(def ASPECT_RATIO (/ IMAGE_WIDTH IMAGE_HEIGHT))

;; Camera settings.
(def CAMERA_ANGLE (make-vec 0 0 90)) ; In degrees.
(def CAMERA_ROTATION_MATRIX (make-rotation-matrix CAMERA_ANGLE))
(def CAMERA_POSITION (make-vec 0 0 0))
(def CAMERA_ZOOM_FACTOR 42.0)

;; How often a ray can bounce.
(def MAX_ITERATIONS 6)

;; Pre-generated data. These vectors are used to create a "cloud of light points" around the actual point light.
;; Using these, the light can shine around edges and appears generally smoother.
(def SOFT_LIGHT_VECTORS [
                         (make-vec -0.86926025814612 0.27294854557358583 -0.3062255151826532)
                         (make-vec 0.7785703670324804 0.38062952153244134 0.2409163387812272)
                         (make-vec 0.5104167745313843 0.4857831940996842 -0.24295363798626357)
                         (make-vec 0.9700951478066362 0.8661311291369704 0.9848455425726841)
                         (make-vec 0.5707996887302362 0.2915385796677801 -0.1470667955657674)
                         (make-vec -0.5728777257957556 -0.6225285602123252 0.6250880561150634)
                         (make-vec 0.06281154834059799 0.1259713550055621 -0.6647042875190123)
                         (make-vec 0.9138081176730986 0.3542274553770144 0.36511504730339217)
                         (make-vec 0.28421600859950136 -0.07267748147456832 0.20953789654427224)
                         (make-vec 0.19931505880414968 0.2601118920434671 -0.8104428795096765)
                         (make-vec -0.8785818267743135 0.8041334206889859 -0.7840047979907048)
                         (make-vec -0.27576150694345336 -0.15735634050797764 -0.07232975024524846)
                         (make-vec -0.25580980905159856 -0.7639040929961582 0.26127813758260454)
                         (make-vec 0.03380044064046284 0.6493862854609422 0.25543575117001005)
                         (make-vec -0.4032750222767827 0.20841618509464888 -0.48555181605243036)
                         (make-vec 0.614836346634213 0.26400741474692513 -0.42536923587177333)
                         (make-vec -0.5277753321798575 -0.26756460044222186 -0.8560303414194823)
                         (make-vec 0.879749521800399 -0.3088318560327725 -0.06320855720063223)
                         (make-vec 0.6092800198023591 -0.7542936956115784 0.8961033289397344)
                         (make-vec -0.9874801706023142 -0.4447038224687485 -0.50343112248979)])

;; Material definitions.
(def STANDARD_MATERIAL
  (make-material 0.01 8.5 2.0 1.0 [1.0, 1.0, 1.0] [1.0, 1.0, 1.0] [1.0, 1.0, 1.0], 0.1))
(def BLUE_MATERIAL
  (make-material 0.02 5.0 3 1.0 [0.5, 0.5, 1.0] [0.5, 0.5, 1.0] [0.5, 0.5, 1.0], 1.25))

;; All scene objects.
(def SCENE_OBJECTS [(make-plane (make-vec -30 0 0) (make-vec 1 0 0) BLUE_MATERIAL)
                    (make-plane (make-vec 30 0 0) (make-vec -1 0 0) STANDARD_MATERIAL)
                    (make-plane (make-vec 0 -30 0) (make-vec 0 1 0) STANDARD_MATERIAL)
                    (make-plane (make-vec 0 30 0) (make-vec 0 -1 0) STANDARD_MATERIAL)
                    (make-plane (make-vec 0 0 -30) (make-vec 0 0 1) STANDARD_MATERIAL)
                    (make-plane (make-vec 0 0 30) (make-vec 0 0 -1) STANDARD_MATERIAL)
                    (make-sphere (make-vec 0 0 0) 7 STANDARD_MATERIAL)
                    (make-sphere (make-vec 11.1 -11.1 0) 3.1 STANDARD_MATERIAL)
                    (make-sphere (make-vec -11.1 11.1 0) 3.1 STANDARD_MATERIAL)
                    (make-sphere (make-vec 14.4 5.4 0) 2.1 STANDARD_MATERIAL)
                    (make-sphere (make-vec -5.4 -14.1 0) 1.2 STANDARD_MATERIAL)])

;; All point lights.
(def SCENE_LIGHTS [(make-light [-28, 14, 3] [0.4 0.8 0.9])
                   (make-light [-29, -29, -29] [1.0 0.2 0.1])
                   (make-light [14, 29, -14] [0.8 0.8 0.2])
                   (make-light [29, 29, 29] [1.0 1.0 1.0])
                   (make-light [28, 0, 29] [0.3 0.6 0.2])])