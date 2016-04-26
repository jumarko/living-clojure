(ns wonderland.ch08)

;;;;
;;;; Living Clojure - Chapter 8: The Power of Macros
;;;;
;;;; Don't use macros if function does the job.
;;;; With functions the parameters are always eagerly evaluated.
;;;; Macros is evaluated at compile time.



;; when is actually a macro like this;
(defmacro when-1
  "Evaluates test. If logical true, evaluates body in an implicit do."
  [test & body]
  (list 'if test (cons 'do body)))

(when-1 (> 1 0) (println "Hello!"))

;; macroexpand can be useful
(macroexpand-1 '(when-1 (= 2 2) (println "It is four!")))


;;; Creating our own macros
(defmacro def-hi-queen [name phrase]
  (list 'defn (symbol name)
        []
        (list 'hi-queen phrase)))

(def-hi-queen alice-hi-queen "My name is Alice")
(alice-hi-queen)

(def-hi-queen march-hare-hi-queen "I'm the March Hare")
(march-hare-hi-queen)

(def-hi-queen white-rabitt-hi-queen"I'm the White Rabbit")
(white-rabitt-hi-queen)

(def-hi-queen mad-hatter-hi-queen "I'm the Mad Hatter")
(mad-hatter-hi-queen)


;;; Using Templating to Create Macros - syntax quote

'(first [1 2 3])
`(first [1 2 3])

(let [x 5]
  '(first [x 2 3]))
(let [x 5]
  `(first [~x 2 3]))

;; we can simplify def-hi-queen macro
(defmacro def-hi-queen [name phrase]
  `(defn ~(symbol name) []
     (hi-queen ~phrase)))
(def-hi-queen dormouse-hi-queen "I am the Dormouse")
(dormouse-hi-queen)
