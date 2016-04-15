(ns wonderland.ch03
  (:require [clojure.set :as set]))

;;;;
;;;; Chapter 3 - State and Concurrency
;;;;


;;; Atoms - designed to store the state of something that is independent of any other state

(def who-atom (atom :caterpillar))
who-atom
;; to see the value we need to derefernce the atom
@who-atom

;; reset! sets the value of atom to new value
(reset! who-atom :chrysalis)
@who-atom

;; swap! uses custom function for changing the value
;; Keep in mind that provided function must be free of side effects, because swap! operator may retry the function if another thread changes the value in the meantime
(def who-atom (atom :caterpillar))

(defn change [state]
  (case state
    :caterpillar :chrysalis
    :chrysalis :butterfly
    :butterfly))

(swap! who-atom change)
(swap! who-atom change)
;; one more time but nothing changes because :butterly is the final state
(swap! who-atom change)


(def counter (atom 0))
@counter

;; use dotimes for side effects (changing the value of atom 5 times)
;; notice the usage of underscore for value that we are not going to use
(dotimes [_ 5] (swap! counter inc))
@counter

;; we can change atom in multiple threads
;; future is simple form for executing a block of code in another thread
(def counter (atom 0))
(defn run-in-thread [n](future (dotimes [_ n] (swap! counter inc))))

(let [n 5]
  (run-in-thread n)
  (run-in-thread n)
  (run-in-thread n) )
@counter

;; we can see what happens when we introduce side effect in swap change function
;; => we'll see some numbers to be printed multiple times
(def counter (atom 0))
(defn inc-print [val]
  (println val)
  (inc val))
(defn run-in-thread [n](future (dotimes [_ n] (swap! counter inc-print))))
(let [n 5]
  (run-in-thread n)
  (run-in-thread n)
  (run-in-thread n) )
@counter


;;; Using Refs for Coordinated Changes
;;; we can use refs when we need to change multiple things in a coordinated fashion
;;; Refs use STM to coordinate changes of state:
;;;   atomicity - updates will occur to all refs, or none of them if something goes wrong
;;;   consistency - optional validator function can be used with refs to check value before transaction commits
;;;   isolation - transaction has its own view of the world

(def alice-height (ref 3))
(def right-hand-bites (ref 10))

@alice-height
@right-hand-bites

;; defines a function that increase Alice's height by 24 inches for every bite
;; Note that alter function must be side effect free, just like it's for swap!
(defn eat-from-right-hand []
  (dosync (when (pos? @right-hand-bites)
            (alter right-hand-bites dec)
            (alter alice-height #(+ % 24)))))

;; to run the function we need a transaction -> ensured by "dosync"
(eat-from-right-hand)
@alice-height
@right-hand-bites

;; let's call the function from 3 threads - two times each
(defn evaluate-in-threads []
  (let [n 2]
    (future (dotimes [_ n] (eat-from-right-hand)))
    (future (dotimes [_ n] (eat-from-right-hand)))
    (future (dotimes [_ n] (eat-from-right-hand)))))

(evaluate-in-threads)

@alice-height
@right-hand-bites

;; alternatively we can use commute function instead of alter.
;; the difference is that commute will not retry during transaction.
;; Instead, it will use in-transaction-value in the meantime, finally setting
;; the ref value at the commit point - function should be commutative or have a last-one-in-wins behavior.
;;
;; Transactions that involve time-consuming computations and a large number of refs are more likely to be retried. This is the reason you might prefer an atom with a map of state over refs.

(def alice-height (ref 3))
(def right-hand-bites (ref 10))

(defn eat-from-right-hand []
  (dosync (when (pos? @right-hand-bites)
            (commute right-hand-bites dec)
            (commute alice-height #(+ % 24)))))

(evaluate-in-threads)

@alice-height
@right-hand-bites

;; use ref-set whenever you have one ref dependent on other one like y = x + 2
(def x (ref 1))
(def y (ref 1))

(defn new-values []
  (dosync
   (alter x inc)
   (ref-set y (+ 2 @x))))

(let [n 2]
  (future (dotimes [_ n] (new-values)))
  (future (dotimes [_ n] (new-values))))
@x
@y



;;; Atoms are used for independent synchronous changes.
;;; Refs are used for coordinates synchronous changes.
;;; Agents are used for independent asynchronous changes => If there's work to be done,
;;; and you don't need the results right away, you can use agents.

(def who-agent (agent :caterpillar))
@who-agent

;; we can use send to change the state of an agent.
;; send takes a function which accept the state of an agent as the first argument.
;; Agent will always process only one action at a time.
;; The actions will be processed in the same order as they were dispatched (if dispatched from the same thread).

;; The previous "change" function should work just fine
(defn change [state]
  ;; sleep just for demonstration that send call is not blocked and value of who-agent is not updated immediately
  (Thread/sleep 5000)
  (case state
    :caterpillar :chrysalis
    :chrysalis :butterfly
    :butterfly))

(send who-agent change)
@who-agent


;; send uses fixed thread pool and thus is good for CPU-bound operations.
;; If there's a possibility that operation can be blocked on IO,
;; you should use send-off which uses expandable thread pool

(def who-agent (agent :caterpillar))
(send-off who-agent change)
@who-agent

;; What happens when agent has an error or exception?
(def who-agent (agent :caterpillar))
(defn change-error [state]
  (throw (Exception. "Boom")))

(send who-agent change-error)
@who-agent
(agent-errors who-agent)

;; agent wills tay in failed state until it is restarted
(restart-agent who-agent :caterpillar)
(send-off who-agent change)
@who-agent

;; you can set different strategies for error handling
(def who-agent (agent :caterpillar))

(defn err-handler-fn [a ex]
  (println "error " ex " value is " @a))

(set-error-mode! who-agent :continue)

;; error handler can be assigned via set-error-handler-fn function
(set-error-handler! who-agent err-handler-fn)

(send who-agent change-error)
@who-agent

;; agent will continue on without a restart
(send who-agent change)
@who-agent
