(ns wonderland.talk
  (:require [camel-snake-kebab.core :as csk]))


(defn serpent-talk [input]
  (str "Serpent! You said: " ( csk/->snake_case  input)))

(defn serpent-kebab-talk [input]
  (str "Serpent! You said: " ( csk/->kebab-case  input)))

(defn serpent-camel-talk [input]
  (str "Serpent! You said: " ( csk/->camelCase  input)))


;;; Running from command line
(defn -main [& args]
  (println (serpent-talk (first args))))
