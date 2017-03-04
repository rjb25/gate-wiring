(defproject wiring "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.8.0"][cljfmt "0.5.1"] ]
  :main wiring.core
  :target-path "target/%s"
  :profiles {:uberjar {:aot :all}})
