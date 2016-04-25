(ns useful-web-libraries.transit-demo
  (:require [cognitect.transit :as transit])
  (:import [java.io ByteArrayInputStream ByteArrayOutputStream]))

;;; write data

(def cat-data {:name "Kitty" :age 2})

(def out (ByteArrayOutputStream. 4096))
(def writer (transit/writer out :json))

(transit/write writer cat-data)
(.toString out)


;;; read data

(def in (ByteArrayInputStream. (.toByteArray out)))
(def reader (transit/reader in :json))

(transit/read reader)
