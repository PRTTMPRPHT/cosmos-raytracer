;;; Contains some utility functions related to saving the generated image.
(ns cosmos.image_util
  (:import [java.awt.image BufferedImage])
  (:require [cosmos.vector3 :refer :all]))

;; The byte order interpretation for the image data.
(def IMAGE_TYPE BufferedImage/TYPE_INT_RGB)

;; Creates a new buffered image.
(defn instantiate-frame-buffer [width, height] (BufferedImage. width height IMAGE_TYPE))

;; Get the coordinates of a pixel on screen.
(defn get-pixel-x [index, imgWidth] (mod index imgWidth))
(defn get-pixel-y [index, imgWidth] (quot index imgWidth))

;; Write operations for the image.
(defn put-pixel-to-framebuffer [fb, x, y, col] (.setRGB fb x y col))
(defn put-pixel-color [fb, index, width, col] (put-pixel-to-framebuffer fb (get-pixel-x index width) (get-pixel-y index width) col))
(defn put-pixel [fb, index, width, colVec] (put-pixel-color fb index width (rgb-to-int colVec)))
