(ns open-cving.core
  (:import [org.opencv.core Mat Size CvType]
           [org.opencv.imgcodecs Imgcodecs]
           [org.opencv.imgproc Imgproc]
           java.awt.image.BufferedImage)
  (:use [seesaw.core]))

(defn show-image-from-file [image-name]
  (let [file-name (str "resources/" image-name ".png")
        file (clojure.java.io/file file-name)]
    (->
      (frame
        :title image-name
        :content (label :icon file))
      pack!
      show!)))

(defn show-image [image title]
  (->
    (frame
      :title (str title)
      :content (label :icon image))
    pack!
    show!))

(defn show-mat [m label]
  (let [channels (. m channels)
        type (if (> channels 1) (. BufferedImage TYPE_3BYTE_BGR) (. BufferedImage TYPE_BYTE_GRAY))
        img (BufferedImage. (. m cols) (. m rows) type)
        data (.. img getRaster getDataBuffer getData)]
    (.get m 0 0 data)
    (show-image img label)))
    ; (.get m 0 0)))
    ; ((. m get) 0 0 (. (. img getRaster getDataBuffer) getDataBuffer))))

(clojure.lang.RT/loadLibrary org.opencv.core.Core/NATIVE_LIBRARY_NAME)

(def lena (Imgcodecs/imread "resources/lena.png" Imgcodecs/IMREAD_GRAYSCALE))
(def blurred (Mat. 512 512 CvType/CV_8UC3))
(show-mat blurred "Blurred")
(Imgproc/GaussianBlur lena blurred (Size. 5 5) 3 3)
(show-mat blurred "Blurred")
(Imgcodecs/imwrite "resources/out.png" blurred)
