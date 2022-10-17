(ns lambdaisland.dotenv
  "Parsing of dotenv files"
  (:require [clojure.string :as str]))

(defn split-at-pred
  "Partition a collection in two, such that in the first partition no element but
  the last satisfies predicate `pred`. Returns `nil` if no element satisfied
  `pred`."
  [pred coll]
  (loop [[y & ys] coll
         xs []]
    (cond
      (pred y)
      [(conj xs y) ys]
      (nil? ys)
      nil
      :else
      (recur ys (conj xs y)))))

(defn parse-hex [h]
  (#?(:clj Long/parseLong :cljs js/parseInt) h 16))

(defn unquote-chars [s]
  (str/replace s #"\\(u[0-9a-fA-F]{4,}|.)"
               (fn [[_ m]]
                 (case m
                   "r" "\r"
                   "n" "\n"
                   "t" "\t"
                   "f" "\f"
                   "b" "\b"
                   (if (= \u (first m))
                     (str (char (parse-hex (subs m 1))))
                     m)))))

(defn expand-vars [s vars]
  (str/replace s #"\$\{([a-zA-Z_]+[a-zA-Z0-9_]*)\}"
               (fn [[_ k]]
                 (str
                  (or (get vars k)
                      #?(:clj (System/getenv k))
                      (throw (ex-info "Failed to expand variable reference" {:value s
                                                                             :unfound-var k})))))))

(defn parse-dotenv
  "Parse the contents of a dotenv file, returns a map.

  - each line is interpreted as VAR_NAME=value
  - leading whitespace, a leading `export` keyword, and whitespace around the `=` sign is ignored
  - lines starting with `#` (and optionally whitespace) are ignored
  - Unix and Windows line endings are understood
  - Values can be unquoted, single-, or double-quoted
  - Quoted values can continue on consequtive lines
  - Unquoted or double-quoted values can contain interpolation using the ${VAR}
    syntax if `{:expand? true}` is set. Values can be interpolated from vars set
    earlier in the contents, passed in as the `:vars` options, or on Clojure (not
    ClojureScript) will be filled from the process environment (using
    `System/getenv`).
  - Certain backslash sequences are understood in unquoted or double-quoted
    values. \\n newline, \\t tab, \\r carriage return, \\f form feed, \\b
    backspace, \uFFFF unicode character. A backslash followed by any other
    character will be replaced by said characters, including single and double
    quotes.
  - In single-quoted values only \\' is understood, other escape sequences are
    treated literally
  "
  ([contents]
   (parse-dotenv contents nil))
  ([contents {:keys [expand? vars]
              :or {vars {}}}]
   (let [lines (str/split contents #"\R")]
     (loop [vars vars
            [line & lines] lines]
       (cond
         (nil? line)
         vars
         (re-find #"^\s*(#.*)?$" line)
         (recur vars lines)
         :else
         (if-let [[_ _ k v] (re-find #"^\s*(export\s+)?([a-zA-Z_]+[a-zA-Z0-9_]*)\s*=\s*(.*)" line)]
           (case (first v)
             \'
             (let [v (subs v 1)
                   [ql rl :as split] (split-at-pred #(re-find #"(^|[^\\])'" %)
                                                    (cons v lines))]
               (when-not split
                 (throw (ex-info "Unterminated quoted value" {:key k})))
               (if-let [[_ ll] (re-find #"(([^']|\\')*)'\s*(#.*)?$" (last ql))]
                 (recur (conj vars {k (str/replace (str/join "\n" (concat (butlast ql) [ll]))
                                                   #"\\'" "'")})
                        rl)
                 (throw (ex-info "Invalid single quoted value" {:key k}))))

             \"
             (let [v (subs v 1)
                   [ql rl :as split] (split-at-pred #(re-find #"(^|[^\\])\"" %)
                                                    (cons v lines))]
               (when-not split
                 (throw (ex-info "Unterminated quoted value" {:key k})))
               (if-let [[_ ll] (re-find #"(([^\"]|\\\")*)\"\s*(#.*)?$" (last ql))]
                 (recur (assoc vars k (cond-> (unquote-chars (str/join "\n" (concat (butlast ql) [ll])))
                                        expand?
                                        (expand-vars vars)))
                        rl)
                 (throw (ex-info "Invalid double quoted value" {:key k}))))

             (recur (assoc vars k (cond-> (unquote-chars (str/trim (str/replace v #"#.*$" "")))
                                    expand?
                                    (expand-vars vars))) lines))
           (throw (ex-info "Invalid syntax" {:line line}))))))))
