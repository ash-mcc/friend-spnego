(defproject ash-mcc/friend-spnego "0.1.0"
  :description "Helps Chas Emerick's Friend library to use the SPNEGO feature of Tobias Sarnowski's Kerberos library."
  :url "https://github.com/ash-mcc/friend-spnego"
  :author "Ashley McClenaghan"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :min-lein-version "2.0.0"
  :dependencies [[org.clojure/clojure "1.5.1"]
                 [org.clojure/tools.logging "0.3.1"]
                 [com.cemerick/friend "0.2.1"]
                 [ash-mcc/sarnowski-kerberos "0.1.0"]]
  :profiles {:dev {:source-paths ["dev"]
                   :dependencies [[http-kit "2.1.13"]
                                  [compojure "1.1.5"]
                                  [hiccup "1.0.1"]]}})