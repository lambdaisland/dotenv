(ns lambdaisland.dotenv-test
  (:require [lambdaisland.dotenv :as dotenv]
            [clojure.test :refer :all]))

(deftest basic-tests
  (is (= {"FOO" "bar"} (dotenv/parse-dotenv "FOO=bar")))
  (is (= {"FOO" "bar"} (dotenv/parse-dotenv "FOO=\"bar\"")))
  (is (= {"FOO" "bar"} (dotenv/parse-dotenv "FOO='bar'")))
  (is (= {"FOO" "bar"} (dotenv/parse-dotenv "export FOO=bar")))
  (is (= {"FOO" "bar"} (dotenv/parse-dotenv "\t FOO=bar  ")))
  (is (= {"FOO" "bar"} (dotenv/parse-dotenv "FOO=\"bar\" #comment")))
  (is (= {"FOO" "bar"} (dotenv/parse-dotenv "FOO=\"bar\" #\"comment\"")))
  (is (= {"FOO" "bar\"xxx\""} (dotenv/parse-dotenv "FOO=\"bar\\\"xxx\\\"\" #\"comment\"")))
  (is (= {"FOO" "bar\n\"xxx\""} (dotenv/parse-dotenv "FOO=\"bar\n\\\"xxx\\\"\" #\"comment\"")))
  ;; Different implemenentations disagree on this one. In shells it's simply not
  ;; valid. We take the point of view that you can backslash-escape a single
  ;; quote, but other backslash sequences are interpreted literally.
  (is (= {"FOO" "bar\n'xxx'"} (dotenv/parse-dotenv "FOO='bar\n\\'xxx\\'' #'comment'"))))

(deftest multiple-vars
  (is (= {"FOO" "bar" "BAR" "baz"} (dotenv/parse-dotenv "FOO=bar\nBAR=baz")))
  (is (= {"FOO" "bar" "BAR" "baz"} (dotenv/parse-dotenv "FOO=bar\n\n  \nBAR=baz\n   ")))
  (is (= {"FOO" "bar" "BAR" "baz"} (dotenv/parse-dotenv "FOO=bar\n\n  #xxx\nBAR=baz\n   "))))

(deftest whitespace-behavior
  (is (= {"FOO1" "bar", "FOO2" "  bar   ", "FOO3" "  bar   "}
         (dotenv/parse-dotenv
          "FOO1=  bar   \nFOO2= \"  bar   \"   \nFOO3= '  bar   '   \n"))))
