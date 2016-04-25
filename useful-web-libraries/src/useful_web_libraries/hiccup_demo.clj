(ns useful-web-libraries.hiccup-demo
  (:require [hiccup.core :refer :all]))

(clojure.pprint/pprint
  (html
    [:h1 "Hi there"]
    [:div.blue "blue div"
     [:div.yellow "yellow div"
      [:div#bob "id bob"]]]))



