(ns useful-web-libraries.enlive-demo
  (:require [net.cgrand.enlive-html :as enlive]))

(def my-snippet (enlive/html
                  "<div id='foo'><p>Buttered Scones</p></div>"))

;; once we have snippet, we can do transformations on it using the "at" form
;; Note that snippet can be defined in external files rather than in code => that way,
;; designers can create and edit them more easily
;; This templating system is flexible anc scales well for bigger web applications
(enlive/at my-snippet [:div#foo] (enlive/html-content
                                   "Marmalade"))