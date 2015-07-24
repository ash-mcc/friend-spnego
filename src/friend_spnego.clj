(ns friend-spnego
  (:require
    [clojure.tools.logging :refer (info)]
    [cemerick.friend :as friend]
    [cemerick.friend.workflow :as friend-wf]
    [sarnowski.kerberos :as kerberos]
    [sarnowski.kerberos.spnego :as spnego]))

(defn wrap-spnego
  "Wraps a SPNEGO inteceptor around the (Ring based web) app.
   jaas-config should contain JAAS login.conf information in the style of a map.
   If it's nil then the SPNEGO inteceptor isn't applied."
  [app jaas-config]
  (if (some? jaas-config)
    (spnego/authenticate 
      app 
      (kerberos/create-service-subject (:principal jaas-config) (kerberos/create-config jaas-config))
      :required? false)
    app))

(defn pre-authentication
  "Registers with Friend, a workflow that checks for evidence of pre-authentication."
  [& {:keys [credential-fn]}]
  (fn [req]
    (if (nil? (:identity (friend/current-authentication req)))
      (when-let [auth-map (credential-fn {:remote-user (:remote-user req)})]
        (friend-wf/make-auth auth-map {::cemerick.friend/workflow :pre-authentication 
                                       ::cemerick.friend/redirect-on-auth? false
                                       ::cemerick.friend/ensure-session true})))))

(defn pre-authentication-credential-fn
  "This credential checker simply checks for evidence of pre-authentication,
   in the form of a suitable :remote-user value."
  [get-roles-fn {:keys [remote-user]}]
  (when-let [username (re-find #"[^@]+" (or remote-user ""))]
    (info "Pre-authentication recognised for:" remote-user)
    {:identity username :roles (get-roles-fn username)}))
