(ns wonderland.ch01
  (:require [clojure.set :as set]))

;;; Simple values

42

12.43

;; rational numbers supported
1/3

4/2
;; 4.0/2 - invalid number

(/ 1 3.0)

"jam"

;; keywords
:jam

;; characters
\j

;; booleans
true
false

nil


;;; Expressions

(+ 1 1)

(+ 1 (+ 8 3))


;;; Lists, vectors, maps, sets

'(1 2 "jam" :marmalade-jar)
;; You an also use commas - not idiomatic
'(1, 2, "jam", :marmalade-jar)

;; Functions for manipulating collections
(first '(:rabbit :pocket-watch :marmalade :door))
(rest '(:rabbit :pocket-watch :marmalade :door))
(first (rest '(:rabbit :pocket-watch :marmalade :door)))
(first (rest (rest '(:rabbit :pocket-watch :marmalade :door))))
(first (rest (rest (rest '(:rabbit :pocket-watch :marmalade :door)))))
(first (rest (rest (rest (rest '(:rabbit :pocket-watch :marmalade :door))))))

;; Constructing lists
(cons 5 '())
(cons 5 nil)
(cons 4 (cons 5 nil))
(cons 3 (cons 4 (cons 5 nil)))
(cons 2 (cons 3 (cons 4 (cons 5 nil))))

;; If you need index access, you need to use vector
[:jar1 1 2 3 :jar2]
(first [:jar1 1 2 3 :jar2])
(rest [:jar1 1 2 3 :jar2])
;; you can use nth with list as well, but vector has better performance
(nth [:jar1 1 2 3 :jar2] 0)
(nth [:jar1 1 2 3 :jar2] 2)
(last [:rabbit :pocket-watch :marmalade])

(count [1 2 3 4])

;; conj adds to the end of vector
(conj [:toast :butter] :jam)
(conj [:toast :butter] :jam :honey)

;; conjs adds to the beginning of list
(conj '(:toast :butter) :jam :honey)

;;; Maps
(def jams  {:jam1 "strawberry" :jam2 "blackberry"})
;; explicit get
(get jams :jam1)
; can be used for specifying the default value
(get jams :jam3 "not found")
;; idiomatic
(:jam1 jams)

(keys jams)
(vals jams)

;; changing the value of the key - always returns a new map
(assoc jams :jam1 "orange")
(dissoc jams :jam1)
(merge jams {:jam1 "blue" :jam3 "red"})


;;; Sets - duplicates are not allowed (IllegalArgumentException)
#{:red :blue :white :pink}

(clojure.set/union #{:r :b :w} #{:w :p :y})
(clojure.set/difference #{:r :b :w} #{:w :p :y})
(clojure.set/intersection #{:r :b :w} #{:w :p :y})

(set [:rabbit :rabbit :watch :door])
(set {:a 1 :b 2 :c 3})

(get  #{:watch :door :rabbit} :rabbit)
(:rabbit  #{:watch :door :rabbit})
(:not-found  #{:watch :door :rabbit})
(#{"rabbit" "monkey"} "rabbit")

(contains? #{:rabbit :door} :rabbit)
(conj #{:rabbit :door} :jam)
(disj #{:rabbit :door} :door)

;;; List - basic data structure in Clojure (LISP)

;; we need to quote the the list
'("marmalade-jar" "empty-jar" "pickle-jam-jar")

;; following is a list of 3 elements
'(+ 1 1)
(first '(+ 1 1))
;; ==> Code is data!


;;; Symbols refer to values
;;; When symbol is evaluated it returns the the things it refers to
;;; def creates a "var" for given symbol (e.g. "developer")
(def developer "Alice")
developer
wonderland.ch01/developer

;; we don't really want to create a global var for all the things.
(let [developer "Alice in Wonderland"]
  developer)
developer


;;; Functions - defn macro
(defn follow-the-rabbit [] "Off we go!")
(follow-the-rabbit)

(defn shop-for-jams [jam1 jam2]
  {:name "jam-basket"
   :jam1 jam1
   :jam2 jam2})
(shop-for-jams "strawberry", "marmalade")

;; anonymous functions
; just the definition
(fn [] (str "Off we go" "!"))
; invocation
((fn [] (str "Off we go" "!")))
; defn macro is the same as follows
(def follow-again (fn [] (str "Off we go" "!")))
(follow-again)
; shorthand
(#(str "Off we go" "!"))
; you can use % to refer to the parameter
(#(str "Off we go " "!" " - " %) "again")
; or %1, %2, etc. if there are multiple parameters
(#(str "Off we go " "!" " - " %1 %2) "again" "?")


;;; Namespaces - keep your symbols organized
;;; Namespaces are organized and controlled access to vars.

;; create new namespace
;; in REPL the namespace is switched automatically - but not in Emacs Live!
(ns alice.favfoods)

;; Returns current namespace
;; The asterisks on the either side of the ns are called "earmuffs"(used for things that are intended for rebinding)
*ns*

;; require
(clojure.set/union #{:r :b :w} #{:w :p :y})
;; clojure.set is auto-required into our namespace when REPL starts up, but we could do it explicitly
(require 'clojure.set)
(require '[clojure.set :as set] )
(set/union #{:r :b :w} #{:w :p :y})

;; Most clojure code will use libs with a require and specify an alias using :as
;; Execptions are the tests, where it is common to use the clojure.test functions directly
;; as well as the namespace you are testing

(defn common-favourite-foods [foods1 foods2]
  (let [food-set1 (set foods1)
        food-set2 (set foods2)
        common-foods (clojure.set/intersection food-set1 food-set2)]
    (str "Common Foods: " common-foods)))

(common-favourite-foods [:jam :brownies :toast]
                        [:lettuce :carrots :jam])
