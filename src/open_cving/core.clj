(ns open-cving.core
  (:import [org.opencv.core Core Mat Size CvType Point Scalar]
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
    ; data))
    (.get m 0 0 data)
    (show-image img label)))
    ; (.get m 0 0)))
    ; ((. m get) 0 0 (. (. img getRaster getDataBuffer) getDataBuffer))))

(clojure.lang.RT/loadLibrary org.opencv.core.Core/NATIVE_LIBRARY_NAME)

; (def lena (Imgcodecs/imread "resources/lena.png" Imgcodecs/IMREAD_GRAYSCALE))
; (def blurred (Mat. 512 512 CvType/CV_8UC3))
; (show-mat blurred "Blurred")
; (Imgproc/GaussianBlur lena blurred (Size. 5 5) 3 3)
; (show-mat blurred "Blurred")
; (Imgcodecs/imwrite "resources/out.png" blurred)

; (show-mat template "Template")
; (show-mat matches "Matches")
(def full (Imgcodecs/imread "resources/rpi_full.jpg"))
(Imgproc/cvtColor full full Imgproc/COLOR_BGR2GRAY)
(def fw (. full cols))
(def fh (. full rows))

(def template (Imgcodecs/imread "resources/rpi_template.jpg" 0))
(def w (. template cols))
(def h (. template rows))

(def matches (Mat. (- fw w) (- fh h) CvType/CV_32FC1))
; (.convertTo matches out Imgproc/COLOR_BGR2GRAY)
(Imgproc/matchTemplate full template matches Imgproc/TM_CCOEFF_NORMED)
(Core/normalize matches matches 0 1 Core/NORM_MINMAX -1 (Mat.))

(def points
  (for [x (range (.cols matches)) y (range (.rows matches))] [x y]))

(defn get-point-value [x y]
  (read-string
    (clojure.string/replace
      (.dump (.row (.col matches x) y))
      #"[^\d\.]"
      "")))

(def treshols 0.9)

(def interests
  (filter
    (fn [[x y]]
      (let [v (get-point-value x y)]
        (> v treshols)))
    points))

(for [[x y] interests]
  (Imgproc/rectangle full (Point. x y) (Point. (+ x w) (+ y h)) (Scalar/all  200), 2))

(show-mat full "Full")

(Imgcodecs/imwrite "resources/rpi_out.png" full)
  ; (.get m 0 0 data))
  ; (show-image img label))
; (def minVal (atom 0.0))
; (def maxVal (atom 0.0))
; (def minLoc (Mat.))
; (def maxLoc (Point.))

; (Imgproc/rectangle full (Point. 10 10) (Point. 100 100) (Scalar/all 128), 2, 8, 0)

; (Core/minMaxLoc matches @minLoc)
; (require '[clojure.reflect :as r])
; (use '[clojure.pprint :only [print-table]])
;
; (print-table (:members (r/reflect point)))
