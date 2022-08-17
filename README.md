# lambdaisland/dotenv

<!-- badges -->
[![cljdoc badge](https://cljdoc.org/badge/com.lambdaisland/dotenv)](https://cljdoc.org/d/com.lambdaisland/dotenv) [![Clojars Project](https://img.shields.io/clojars/v/com.lambdaisland/dotenv.svg)](https://clojars.org/com.lambdaisland/dotenv)
<!-- /badges -->

Pure Clojure/ClojureScript parser of dotenv file syntax.

## Features

Parse the contents of a dotenv file, returns a map.

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
- Certain backslash sequences are understood. \\n newline, \\t tab, \\n
  newline, \\r carriage return, \\f form feed, \\b backspace, \uFFFF unicode
  character. A backslash followed by any other character will be replaced by
  said characters, including single and double quotes.
  
<!-- installation -->
## Installation

To use the latest release, add the following to your `deps.edn` ([Clojure CLI](https://clojure.org/guides/deps_and_cli))

```
com.lambdaisland/dotenv {:mvn/version "0.1.1"}
```

or add the following to your `project.clj` ([Leiningen](https://leiningen.org/))

```
[com.lambdaisland/dotenv "0.1.1"]
```
<!-- /installation -->

## Usage

```clj
(require '[lambdaisland.dotenv :as dotenv])

(dotenv/parse-dotenv
 "FOO=xxx
BAR=\"${FOO} hello ${FOO}\"
FILE_PATH=${HOME}/my_file"
 {:expand? true})
;; => {"FOO" "xxx", "BAR" "xxx hello xxx", "FILE_PATH" "/home/arne/my_file"}
```

<!-- opencollective -->
## Lambda Island Open Source

<img align="left" src="https://github.com/lambdaisland/open-source/raw/master/artwork/lighthouse_readme.png">

&nbsp;

dotenv is part of a growing collection of quality Clojure libraries created and maintained
by the fine folks at [Gaiwan](https://gaiwan.co).

Pay it forward by [becoming a backer on our Open Collective](http://opencollective.com/lambda-island),
so that we may continue to enjoy a thriving Clojure ecosystem.

You can find an overview of our projects at [lambdaisland/open-source](https://github.com/lambdaisland/open-source).

&nbsp;

&nbsp;
<!-- /opencollective -->

<!-- contributing -->
## Contributing

Everyone has a right to submit patches to dotenv, and thus become a contributor.

Contributors MUST

- adhere to the [LambdaIsland Clojure Style Guide](https://nextjournal.com/lambdaisland/clojure-style-guide)
- write patches that solve a problem. Start by stating the problem, then supply a minimal solution. `*`
- agree to license their contributions as MPL 2.0.
- not break the contract with downstream consumers. `**`
- not break the tests.

Contributors SHOULD

- update the CHANGELOG and README.
- add tests for new functionality.

If you submit a pull request that adheres to these rules, then it will almost
certainly be merged immediately. However some things may require more
consideration. If you add new dependencies, or significantly increase the API
surface, then we need to decide if these changes are in line with the project's
goals. In this case you can start by [writing a pitch](https://nextjournal.com/lambdaisland/pitch-template),
and collecting feedback on it.

`*` This goes for features too, a feature needs to solve a problem. State the problem it solves, then supply a minimal solution.

`**` As long as this project has not seen a public release (i.e. is not on Clojars)
we may still consider making breaking changes, if there is consensus that the
changes are justified.
<!-- /contributing -->

<!-- license -->
## License

Copyright &copy; 2022 Arne Brasseur and Contributors

Licensed under the term of the Mozilla Public License 2.0, see LICENSE.
<!-- /license -->
