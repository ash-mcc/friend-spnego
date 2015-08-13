# Friend-SPNEGO

A small library that helps Chas Emerick's Friend library to use the SPNEGO feature 
of Tobias Sarnowski's Kerberos library.

## Example use
    
Here is a snippet of code that shows the use of the `pre-authentication-credential-fn`,
`pre-authentication` and `wrap-spnego` functions from this library.

```clojure
(let [get-roles-fn (fn [username] #{::user})
      map-remote-user-fn (fn [remote-user] (re-find #"[^@]+" (or remote-user "")))
      credential-fn (partial pre-authentication-credential-fn map-remote-user-fn get-roles-fn)
      auth-config {:workflows [(pre-authentication :credential-fn credential-fn)]}
      jaas-config {:userKeyTab true
                   :keyTab "test/http.host1.keytab"
                   :principal "HTTP/host1.acme.org.uk@ACME"
                   :storeKey true
                   :isInitiator false}
      exempt-fn (fn [request] (= "/favicon.ico" (:uri request)))]
  (wrap-spnego  
    (authenticate        
      (mk-app-to-be-secured) 
      auth-config)
    jaas-config
    :exempt? exempt-fn))
```
where:
* `get-roles-fn` outputs a (possibly empty) collection of roles when given a username.
* `map-remote-user-fn` maps remote-user values to values that are to be used as Friend :identity values 
    (the example above will map ashley.mcclenaghan@acme.org.uk --to-> ashley.mcclenaghan).
* `jaas-config` is a JAAS login.conf information in the style of a map.
* `exempt-fn` (an optional, named argument) decides if a request should be exempted from being processed for SPNEGO
    (the example above says that requests for /favicon.ico should be exempt)

## License

Distributed under the Eclipse Public License, the same as Clojure.
