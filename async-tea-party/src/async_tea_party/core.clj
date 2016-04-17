(ns async-tea-party.core
  (:gen-class)
  (:require [clojure.core.async :as async]))

;;; core.async basics - channels
;;;
;;; With Channels we have the ability to wait for input,
;;; go blocks are not bound to threads (=> we can have many of them)

;; create a channel
(def tea-channel (async/chan))


;; synchronous put and take
;; beware to specify buffer size first, otherwise the put will be blocked until take is called
;; when we specify size, put won't be blocked unless the buffer is full.
(def tea-channel (async/chan 10))
(async/>!! tea-channel :cup-of-tea)
;; get if off again
(async/<!! tea-channel)
;; don't try to take again while the channel is open -> it will block

;; but we can close channel to avoid further puts
;; still, the values can be taken from channel, until it's empty
(async/>!! tea-channel :cup-of-tea-2)
(async/>!! tea-channel :cup-of-tea-3)
(async/>!! tea-channel :cup-of-tea-4)
(async/close! tea-channel)
;; notice that we can't put another value
(async/>!! tea-channel :cup-of-tea-5)

(async/<!! tea-channel)
(async/<!! tea-channel)
(async/<!! tea-channel)
(async/<!! tea-channel);; => nil


;; asynchronous put and take - need to be wrapped by async/go
(let [tea-channel (async/chan)]
  (async/go (async/>! tea-channel :cup-of-tea-1))
  (async/go (println "Thanks for the " (async/<! tea-channel))))

;; we can set up a go-loop that will continuously execute
(def tea-channel (async/chan 10))
(async/go-loop []
  (println "Thanks for the " (async/<! tea-channel))
  (recur))
(async/>!! tea-channel :hot-cup-of-tea)
(async/>!! tea-channel :tea-with-sugar)
(async/>!! tea-channel :tea-with-milk)


;; we can also look at values from multiple channels
;; and use values from the one that arrives first - using alts!
(def tea-channel (async/chan 10))
(def milk-channel (async/chan 10))
(def sugar-channel (async/chan 10))
(async/go-loop []
  (let [ [v ch] (async/alts! [tea-channel
                              milk-channel
                              sugar-channel])]
    (println (str "Got " v " from " ch))
    (recur)))
(async/>!! sugar-channel :sugar)
(async/>!! milk-channel :milk)
(async/>!! tea-channel :tea)



;;; Serving Tea at a core.async Tea Party
;;; Asks Google Tea Service and Yahoo Tea Service and returns the fastest response

(def google-tea-service-chan (async/chan 10))
(def yahoo-tea-service-chan (async/chan 10))
;; random-add simulates the computation with random duration
(defn random-add []
  (reduce + (conj [] (repeat 1 (rand-int 100000)))))

(defn request-google-tea-service []
  (async/go
    (random-add)
    (async/>! google-tea-service-chan "tea compliments of google")))

(defn request-yahoo-tea-service []
  (async/go
    (random-add)
    (async/>! yahoo-tea-service-chan "tea compliments of yahoo")))

(defn request-tea []
  (request-google-tea-service)
  (request-yahoo-tea-service)
  (async/go (let [[v] (async/alts! [google-tea-service-chan yahoo-tea-service-chan])]
              (println v))))
(request-tea)
(request-tea)
(request-tea)
(request-tea)
(request-tea)
