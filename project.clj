(defproject ash-mcc/friend-spnego "0.1.8"
  :description "Helps Chas Emerick's Friend library to use the SPNEGO feature of Tobias Sarnowski's Kerberos library."
  :url "https://github.com/ash-mcc/friend-spnego"
  :author "Ashley McClenaghan"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :min-lein-version "2.0.0"
  :dependencies [[org.clojure/clojure "1.6.0"]
                 [org.clojure/tools.logging "0.3.1"]
                 [com.cemerick/friend "0.2.1"]
                 [ash-mcc/sarnowski-kerberos "0.3.0"]]
  :profiles {:dev {:dependencies [[ring/ring-core "1.3.2"]
                                  [ring/ring-jetty-adapter "1.3.2"]]}})
