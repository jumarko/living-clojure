(ns wonderland.talk-test
  (:require [wonderland.talk :refer :all]
            [clojure.test :refer :all]))

(deftest test-serpent-talk
  (testing "Cries serpent! with a snake_case version of the input"
    (is (= "Serpent! You said: hello_there"
           (serpent-talk "hello there"))))

  (testing "kebab case"
    (is (= "Serpent! You said: hello-there"
           (serpent-kebab-talk "hello there"))))

  (testing "camel case"
    (is (= "Serpent! You said: helloThere"
           (serpent-camel-talk "hello there")))))
