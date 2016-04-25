(ns useful-web-libraries.liberator-demo
  (:require
    [liberator.core :refer [resource]]
    [compojure.core :refer :all]
    [ring.middleware.defaults :refer [wrap-defaults site-defaults]]))


(defroutes app-routes
           (ANY "/cat" []
             (resource :available-media-types ["text/plain"
                                                   "text/html"
                                                   "application/json"]
                           :handle-ok
                           #(let [media-type (get-in % [:representation :media-type])]
                             (case media-type
                               "text/plain" "Cat"
                               "text/html" "<html><h2>Cat</h2></html>"
                               "application/json" {:cat true}))
                           :handle-not-acceptable "No Cats Here!")))

(def app
  (-> app-routes
      (wrap-defaults site-defaults)))

