;;   Copyright (c) 7theta. All rights reserved.
;;   The use and distribution terms for this software are covered by the
;;   Eclipse Public License 1.0 (http://www.eclipse.org/legal/epl-v10.html)
;;   which can be found in the LICENSE file at the root of this
;;   distribution.
;;
;;   By using this software in any fashion, you are agreeing to be bound by
;;   the terms of this license.
;;   You must not remove this notice, or any others, from this software.

(defproject com.7theta/re-frame-via "0.3.0"
  :description "A re-frame library for via WebSocket based messaging"
  :url "https://github.com/7theta/re-frame-via"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[re-frame "0.10.5"]
                 [buddy/buddy-auth "2.1.0"]
                 [buddy/buddy-hashers "1.3.0"]
                 [com.7theta/via "0.7.4"]]
  :source-paths ["src/clj" "src/cljs"]
  :profiles {:dev {:dependencies [[org.clojure/clojure "1.9.0"]
                                  [org.clojure/clojurescript "1.10.238"]

                                  [reagent "0.8.0-alpha2"
                                   :exclusions [cljsjs/react cljsjs/react-dom
                                                cljsjs/react-dom-server]]
                                  [cljsjs/react-dom "16.3.0-1"]
                                  [cljsjs/react "16.3.0-1"]

                                  [http-kit "2.2.0"]
                                  [ring/ring-core "1.6.3" :exclusions [commons-codec]]
                                  [ring/ring-defaults "0.3.1"]
                                  [ring/ring-anti-forgery "1.2.0"]
                                  [compojure "1.6.0"]

                                  [integrant "0.6.3"]
                                  [yogthos/config "1.1.1"]

                                  [binaryage/devtools "0.9.9"]
                                  [figwheel-sidecar "0.5.15"]
                                  [com.cemerick/piggieback "0.2.2"]
                                  [integrant/repl "0.3.1"]]
                   :source-paths ["example/src/clj" "example/dev/clj"]
                   :resource-paths ["example/resources"]
                   :clean-targets ^{:protect false} ["example/resources/public/js/compiled" "target"]
                   :repl-options {:nrepl-middleware [cemerick.piggieback/wrap-cljs-repl]}
                   :plugins [[lein-cljsbuild "1.1.7" :exclusions [org.apache.commons/commons-compress]]
                             [lein-figwheel "0.5.15" :exclusions [org.clojure/clojure]]]}}
  :cljsbuild {:builds [{:id "dev"
                        :source-paths ["example/src/cljs" "example/dev/cljs"]
                        :figwheel {:on-jsload "example.core/mount-root"}
                        :compiler {:main example.core
                                   :output-to "example/resources/public/js/compiled/app.js"
                                   :output-dir "example/resources/public/js/compiled/out"
                                   :asset-path "js/compiled/out"
                                   :source-map-timestamp true
                                   :preloads [devtools.preload]
                                   :external-config {:devtools/config {:features-to-install :all}}}}]}
  :prep-tasks ["compile"]
  :scm {:name "git"
        :url "https://github.com/7theta/re-frame-via"})
