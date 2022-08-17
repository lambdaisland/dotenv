(ns poke
  (:require [lambdaisland.dotenv :as dotenv]))

(require '[lambdaisland.dotenv :as dotenv])

(dotenv/parse-dotenv
 "FOO=xxx
BAR=\"${FOO} hello ${FOO}\"
FILE_PATH=${HOME}/my_file"
 {:expand? true})
;; => {"FOO" "xxx", "BAR" "xxx hello xxx", "FILE_PATH" "/home/arne/my_file"}
