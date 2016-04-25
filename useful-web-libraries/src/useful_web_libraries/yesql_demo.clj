(ns useful-web-libraries.yesql-demo
  (:require [yesql.core :refer [defqueries]]))

(def db-spec {:classname ""
              :subprotocol "mysql"
              :subname "//localhost:3306/test"
              :user "me"})

;; make sure that "sql" directory is directly on your classpath
;; Check http://stackoverflow.com/questions/28193241/clojure-yesql-not-able-to-find-queries-file in case of issues
(defqueries "yesql_demo.sql" {:connection db-spec})

(get-users)
(get-user-by-first-name {:first_name "Juraj"})
