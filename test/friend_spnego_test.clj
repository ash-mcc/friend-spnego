(ns friend-spnego-test
  (:require
    [friend-spnego :refer (pre-authentication-credential-fn pre-authentication wrap-spnego)]
    [cemerick.friend :refer (authenticate)]
    [ring.adapter.jetty :refer (run-jetty)]
    [clojure.test :refer :all]))

(defn mk-app-to-be-secured []
  (run-jetty
    (fn [req] {:status 200 :headers {"Content-Type" "text/plain"} :body "this is the app"})
    {:port 8000}))

(deftest touch-all
  (let [get-roles-fn (fn [username] #{::user})
        map-remote-user-fn (fn [remote-user] (re-find #"[^@]+" (or remote-user "")))
        credential-fn (partial pre-authentication-credential-fn map-remote-user-fn get-roles-fn)
        auth-config {:workflows [(pre-authentication :credential-fn credential-fn)]}
        jaas-config {:userKeyTab true
                     :keyTab "test/http.host1.keytab"
                     :principal "HTTP/host1.acme.org.uk@ACME"
                     :storeKey true
                     :isInitiator false}]
    (wrap-spnego  
      (authenticate 
        (mk-app-to-be-secured) 
        auth-config)
      jaas-config)))