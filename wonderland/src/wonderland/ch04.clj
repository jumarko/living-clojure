(ns wonderland.ch04
  (:import (java.net InetAddress)))

;;;;
;;;; Java Interop and Polymorphism
;;;;
;;;; Clojure runs on JVM which is very matured platform with tons of useful libraries.
;;;; Therefore, not every library need to be implemented in Clojure itself
;;;; and many Clojure libraries use java libraries under the hood.



;;; Handling Interop with Java

;; Clojure string is instance of java.lang.String
(class "caterpillar")

;; we can invoke Java methods like this
(. "caterpillar" toUpperCase)
;; or
(.toUpperCase "caterpillar")

;; if java method takes arguments, they are included after the object
(.indexOf "caterpillar" "pillar")

;; Instances can be created with new
(new String "Hi!!")
;; or
(String. "Hi!!")
;; Note that it's usually not sane to create new instance of String - just use ""

;; Java classes can be imported via :import in namespace declaration
(InetAddress/getByName "localhost")

;; doto macro can be useful if we have a series of operations that we need to do on Java object
(def sb (doto (StringBuilder. "Who ")
          (.append "are ")
          (.append "you?")))
(.toString sb)
;; much nicer than nested syntax version
(def sb
  (.append
   (.append
    (StringBuilder. "Who ")
    "are ")
   "you?"))
(.toString sb)

(import 'java.util.UUID)
(UUID/randomUUID)



;;; Practical Polymorphism
;;;
;;; Clojure, in contrast to Java, has small amount of types and many different functions for them.
;;; However, Clojure realizes that polymorphism is flexible and useful in some situations.

;; E.g. we could implement different behavior for different inputs using the case statement
(defn who-are-you [input]
  (cond
   (= java.lang.String (class input)) "String - who are you?"
   (= clojure.lang.Keyword (class input)) "Keyword - who are you?"
   (= java.lang.Long (class input)) "Long - who are you?"))
(who-are-you :alice)
(who-are-you "alice")
(who-are-you 123)
(who-are-you true)

;; This polymorphism can be expressed with multimethods.

;; We first need to define multimethod and how it is going to dispatch (in our case the "class" function)
(defmulti who-are-you class)
(defmethod who-are-you java.lang.String [input]
  (str "String - who are you? " input))
(defmethod who-are-you clojure.lang.Keyword [input]
  (str "Keyword - who are you? " input))
(defmethod who-are-you java.lang.Long [input]
  (str "Long - who are you? " input))

(who-are-you :alice)
(who-are-you "alice")
(who-are-you 123)
;; Unknown type of argument throws IllegalArgumentException
(who-are-you true)

;; we can specify the default case
(defmethod who-are-you :default [input]
  (str "I don't know - who are you? " input))
(who-are-you true)

;; introduce multimethod for handling conversation between caterpillar and Alice
(defmulti eat-mushroom (fn [height]
                         (if (< height 3)
                           :grow
                           :shrink)))
;; no we need defmethod-s for :grow and :shrink
;; Notice the usage of underscore instead of input argument - this means that we don't care about it -> we don't want to use it at all in method definition
(defmethod eat-mushroom :grow [_]
  "Eat the right side to grow")
(defmethod eat-mushroom :shrink [_]
  "Eat the left side to shrink")

(eat-mushroom 1)
(eat-mushroom 3)
(eat-mushroom 9)


;; Protocols are another way to handle polymorphism
;; Multimehods are good for for polymorphism on one function,
;; protocols can handle polymorphism for group of functions.

(defprotocol BigMushroom
  (eat-mushroom [this]))

;; Next, we implement the protocol for all our types at once using extend-protocol
(extend-protocol BigMushroom
  java.lang.String
  (eat-mushroom [this]
    (str (.toUpperCase this) " mmmm tasty!"))

  clojure.lang.Keyword
  (eat-mushroom [this]
    (case this
      :grow "Eat the right side!"
      :shrink "Eath the left side!"))

  java.lang.Long
  (eat-mushroom [this]
    (if (< this 3)
      "Eat the right side to grow"
      "Eat the left side to shrink")))

(eat-mushroom "Big Mushroom")
(eat-mushroom :grow)
(eat-mushroom 1)


;; What if we want to add our own data structure?
;; => If you need structured data, the answer is to use defrecord, which creates a class with new type
(defrecord Mushroom [color height])
;; now we can create a new mushroom object
(def regular-mushroom  (Mushroom. "white and blue polka dots" "2 inches"))
regular-mushroom
(class regular-mushroom)

;; to access fields we can use dot-dash ".-" which is preferred over dot-prefix "."
(.-color regular-mushroom)
(.-height regular-mushroom)
;; following throws IllegalArgumentException - "No maching field found"
(.-width regular-mushroom)

;; Let's define Edible protocol
(defprotocol Edible
  (bite-right-side [this])
  (bite-left-side [this]))

;; and we can start having records that implement that protocol
(defrecord WonderlandMushroom [color height]
  Edible
  (bite-right-side [this]
    (str "The " color " bite makes you grow bigger"))
  (bite-left-side [this]
    (str "The " color " bite makes you grow smaller")))

(defrecord RegularMushroom [color height]
  Edible
  (bite-right-side [this]
    (str "The " color " bite tastes bad"))
  (bite-left-side [this]
    (str "The " color " bite tastes bad too")))

(def alice-mushroom (WonderlandMushroom. "blue dots" "3 inches"))
(def reg-mushroom (RegularMushroom. "brown" "1 inches"))

(bite-right-side alice-mushroom)
(bite-left-side alice-mushroom)

(bite-right-side reg-mushroom)
(bite-left-side reg-mushroom)


;; Real-World examples for protocols
;; Implementing different types for persistence.
;; We can write information to different types of data sources - database, Amazon S3, etc.


;; when we don't care about the structure or the map lookup features provided by defrecord and we just need an object with a type to save memory, we should reach for deftype

(deftype WonderlandMushroom []
  Edible
  (bite-right-side [this]
    (str "The bite makes you grow bigger"))
  (bite-left-side [this]
    (str "The bite makes you grow smaller")))

(deftype RegularMushroom []
  Edible
  (bite-right-side [this]
    (str "The bite tastes bad"))
  (bite-left-side [this]
    (str "The bite tastes bad too")))

(def alice-mushroom (WonderlandMushroom.))
(def reg-mushroom (RegularMushroom.))

(bite-right-side alice-mushroom)
(bite-left-side alice-mushroom)

(bite-right-side reg-mushroom)
(bite-left-side reg-mushroom)


;; To sum up:
;; If you want structured data, use defrecord which provies type-based dispatch and you can still manipulate your data like maps which is greate for reuse.
;; Sometimes, this is not needed so you can use deftype in those cases.
;;
;; Protocols and data types are powerful feature but beware!
;; Many people coming from OO world tend to use them too often.
;; THINK BEFORE YOU USE PROTOCOLS!
;;
;; In most cases, a pure function or multimethod can be used instead.
;; Clojure makes move from just maps to records easy, so you can delay the decision of whether or not to use protocols.

(defn bite-right-side [mushroom]
  (if (= (:type mushroom) "wonderland")
    "The bite makes you grow bigger"
    "The bite tastes bad"))

(defn bite-left-side [mushroom]
  (if (= (:type mushroom) "wonderland")
    "The bite makes you grow smaller"
    "The bite tastes bad too"))

(bite-right-side {:type "wonderland"})
(bite-left-side {:type "wonderland"})

(bite-right-side {:type "regular"})
(bite-left-side {:type "regular"})
