(ns wonderland.ch02
  (:require [clojure.set :as set]))

;;;;
;;;; Chapter 2 - Flow and Functional transformations
;;;;


;;; Expressions vs Forms
;;; expression = code that can be evaluated for a result
;;; from = a valid expression that can be evaluated


;;; Controling the Flow with Logic

(class true)
(true? true)
(true? false)
(true? (Boolean. "true"))

(false? false)

(false? true)
(false? (Boolean. "false"))

(nil? nil)
(nil? 1)

(not true)
(not false)

;; equality
(= :drinkme :drinkme)
(= :drinkme 4)
(= '(:drinkme :bottle) [:drinkme :bottle])
(not= :drinkme 4)

;; Logic tests that you can use on collections
(empty? [:table :door :key])
(empty? [])
(empty? {})
(empty? '())

;; empty? is implemented via (not (seq coll))
;; => we can use seq for checking if collection is not empty
(seq nil)
(seq [])
(seq [1 2])

(defn drinkable?  [x]
  (= x :drinkme))

(every? drinkable? [:drinkme :drinkme])
(every? drinkable? [:drinkme :poison])
;; or use anonymous function
(every? #(drinkable? %) [:drinkme :drinkme])

(not-any? drinkable? [:drinkme :drinkme])
(not-any? drinkable? [:drinkme :poison])
(not-any? drinkable? [:poison :poison])

(some #(> % 3) [1 2 3 4 5])
(some #{3} [1 2 3 4 5])



;;; flow Control - if, cond, case

(if true "it is true" "it is false")
(if false "it is true" "it is false")
(if nil "it is true" "it is false")

(if-let [need-to-grow-small (> 5 3)]
  "drink bottle"
  "don't drink bottle")

(let [bottle "drinkme"]
  (cond
   (= bottle "poison") "don't touch"
   (= bottle "drinkme") "grow smaller"
   (= bottle "empty") "all gone"
   ))

;; order is important !
(let [x 11]
  (cond
   (> x 3) "bigger than 3"
   (> x 10) "bigger than 10" ; won't work because "bigger than 3" always match first
   (> x 4) "bigger than 4"
   ))

;; use :else for default clause

(let [bottle "mystery"]
  (cond
   (= bottle "poison") "don't touch"
   (= bottle "drinkme") "grow smaller"
   (= bottle "empty") "all gone"
   :else "unknown"
   ))

;; if you are using just one test value and = comparison, then case is more concise
(let [bottle "drinkme"]
  (case bottle
    "poison" "don't touch"
    "drinkme" "grow smaller"
    "empty" "all gone")
  )

;; if no match is found, the behavior of case is much different than the cond => RETURN ERROR
(let [bottle "mystery"]
  (case bottle
    "poison" "don't touch"
    "drinkme" "grow smaller"
    "empty" "all gone")
  )

;; you can still specify the default for case
(let [bottle "mystery"]
  (case bottle
    "poison" "don't touch"
    "drinkme" "grow smaller"
    "empty" "all gone"
    "unknown")
  )

;; there's also special variant of cond - condp - which you can use when test predicate is the same
(let [x 5]
  (condp > x
    10 "Bigger than 10"
    4 "Bigger than 4"
    3 "Bigger than 3"
    ))

;; following doesn't make much sense but it's possible
(let [x 5]
  (condp >
    x 10 "Bigger than 10"
    x 4 "Bigger than 4"
    x 3 "Bigger than 3"
    ))



;;; Functions creating functions and other nested expressions
(defn grow [name direction]
  (if (= direction :small)
    (str name " is growing smaller")
    (str name " is growing bigger")))

(grow "Alice" :big)

(grow "Alice" :small)

;; partial application
(def grow-alice ( partial grow "Alice"))

(grow-alice :small)

;; comp - combine multiple functions to one
(defn toogle-grow [direction]
  (if (= direction :small)
    :big
    :small))

(toogle-grow :big)
(toogle-grow :small)

(defn oh-my [direction]
  (str "Oh My! You are growing " direction))

;; we can just call one function after another
(oh-my (toogle-grow :small))

;; or we could use comp
((comp oh-my toogle-grow) :small)



;;; Destructuring

(let [[color size] ["blue" "small"]]
  (str "The " color " door is " size))

;; without destructuring
(let [x ["blue" "small"]
      color (first x)
      size (last x)]
  (str "The " color " door is " size))

;; vector destructuring is able to handle nested vectors easily
(let [[color [size]] ["blue" ["very small"]]]
  (str "The " color " door is " size))

;; we can keep a reference to complete structure using ":as"
(let [[color [size] :as original] ["blue" ["small"]]]
  {:color color :size size :original original})

;; destructuring using map
(let [{flower1 :flower1 flower2 :flower2} {:flower1 "red" :flower2 "blue"}]
  (str "The flowers are " flower1 " and " flower2))

;; you can specify default value using ":or"
(let [{flower1 :flower1 flower2 :flower2 :or {flower2 "missing"}} {:flower1 "red"}]
  (str "The flowers are " flower1 " and " flower2))

;; ":keys" shortcut
(let [{:keys [flower1 flower2]} {:flower1 "red" :flower2 "blue"}]
  (str "The flowers are " flower1 " and " flower2))

;; we can use destructuring in function parameters as well - it can be a great tool for documentation
(defn flower-colors [{:keys [flower1 flower2]}]
  (str "The flowers are " flower1 " and " flower2))
(flower-colors {:flower1 "red" :flower2 "blue"})



;;; Power of Laziness
(take 5 (range))
(take 10 (range))

(range 5)
;; class is clojure.lang.LongRange rather than clojure.lang.LazySeq mentioned in book
(class (range 5))

(count (take 100000 (range)))

;; Other ways how to generate infinite sequences

(repeat 3 "rabbit")
(class (repeat 3 "rabbit"))

(take 5 (repeat "rabbit"))
(count (take 5000 (repeat "rabbit")))

(rand-int 10)
(repeat 5 (rand-int 10))
;; need to use repeatedly in this case
(repeatedly 5 #(rand-int 10))
(take 10 (repeatedly #(rand-int 10)))

(take 3 (cycle ["big" "small"]) )
(take 3 (rest (cycle ["big" "small"])))



;;; Recursion
(def adjs ["normal" "too small" "too big" "swimming"])
;; we want to transform input using function #(str "Alice is " %)
(defn alice-is [in out]
  (if (empty? in)
    out
    (alice-is
     (rest in)
     (conj out (str "Alice is " (first in)))))
  )
(alice-is adjs [])

;; In Clojure we can make things easier using loop-recur
(defn alice-is-2 [input]
  (loop [in input
         out []]
    (if (empty? in)
      out
      (recur (rest in)
             (conj out (str "Alice is " (first in)))))
    ))
(alice-is-2 adjs)

;; loop-recur also provides a way for tail recursion optimization -> avoids stack overflow
(defn countdown [n]
  (if (= n 0)
    n
    (countdown (- n 1))))
(countdown 100000)
;; VS.
(defn countdown-2 [n]
  (if (= n 0)
    n
    (recur (- n 1))))
(countdown-2 100000)


;;; The Functional Shape of Data Transformations
(def animals ["mouse" "duck" "dodo" "lory" "eaglet"])

(map #(str %) animals)
;; map returns lazy sequence
(class (map #(str %) animals))
;; so we can process functions against infinite sequences!
(take 10 (map #(str %) (range)))

;; Be careful when combinging laziness with side effects
;; Notice that nothing happens when following symbol is defined.
(def animal-print (map #(println %) animals))
;; it needs to be evaluated to see printlns to console
animal-print

;; We can force the evaluation by using doall
(def animal-print (doall (map #(println %) animals)))


;; Map can take more than one collection
(def colors ["brown" "black" "blue" "pink" "gold"])
(defn gen-animal-string [animal color]
  (str color "-" animal))
(map gen-animal-string animals colors)

;; map function will terminate when the shortest collection ends
(def colors [ "brown" "black"])
(map gen-animal-string animals colors)
;; thus we can use infinite collection
(map gen-animal-string animals (cycle ["brown" "black"]))



;;; Reduce the Ultimate
(reduce + [1 2 3 4 5])

(reduce (fn [r x] (+ r (* x x))) [1 2 3])

;; we can change the shape of result by specifying the initial val to be empty vector
(def animals-with-nils [:mouse nil :duck nil nil :lory])
(reduce (fn [r x] (if (nil? x) r (conj r x)))
        []
        animals-with-nils)

;; unlike map, the reduce cannot process infinite sequence



;;; Other useful data shaping expressions

;; using filter we can remove nil elements in a more elegant way than using reduce
(filter (complement nil?) animals-with-nils)
;; in our case we can also use keyword? function
(filter keyword? animals-with-nils)
;; or use remove function
(def animals-without-nils ( remove nil? animals-with-nils))

;; for is very useful form
(for [animal animals-without-nils]
  (str (name animal)))

;; we can iterate over multiple collections as well
(for [animal animals-without-nils
      color [:red :blue]]
  (str (name color) (name animal)))

;; there are useful modifiers like :let
(for [animal animals-without-nils
      color [:red :blue]
      :let [animal-str (str "animal-" (name animal))
            color-str (str "color-" (name color))
            display-str (str animal-str "--" color-str)]]
  display-str)

;; or :when modifier
(for [animal animals-without-nils
      color [:red :blue]
      :let [animal-str (str "animal-" (name animal))
            color-str (str "color-" (name color))
            display-str (str animal-str "--" color-str)]
      :when (= color :blue)]
  display-str)

;; flatten for nested collections
(flatten [ [:duck [:mouse]] [[:lory]]])


;; we can change the form of the data structure - list to vector, etc
(vec '(1 2 3))
(into [] '(1 2 3))

(sorted-map :b 2 :a 1 :z 3)
(into (sorted-map) {:b 2 :c 3 :a 1})
(into {} [[:a 1] [:b 2] [:c 3]])

(into [] {:a 1 :b 2 :c 3})

;; partitioning
(partition 3 (range 1 10))
;; by default, the remainder is cut off
(partition 3 (range 1 11))
;; but we can use partition-all
(partition-all 3 (range 1 11))

;; partition-by takes custom partitioning function => creates new partition every time the result changes
(partition-by #(= 6 %) (range 1 11))
