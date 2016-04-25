(defproject useful-web-libraries "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "https://github.com/jumarko/living-clojure/tree/master/useful-web-libraries"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.8.0"]
                 [hiccup "1.0.5"]
                 [enlive "1.1.6"]
                 [liberator "0.14.1"]
                 [compojure "1.4.0"]
                 [ring/ring-defaults "0.1.5"]
                 [com.cognitect/transit-clj "0.8.285"]
                 [yesql "0.5.2"]
                 [mysql/mysql-connector-java "5.1.32"]]
  :plugins [[LEIN-RING "0.9.7"]]
  :ring {:handler useful-web-libraries.liberator-demo/app}
  :target-path "target/%s"
  :profiles {:uberjar {:aot :all}})
