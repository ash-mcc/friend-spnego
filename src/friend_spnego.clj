(ns friend-spnego
  (:require
    [clojure.tools.logging :refer (info)]
    [cemerick.friend :as friend]
    [cemerick.friend.workflows :as friend-wf]
    [sarnowski.kerberos :as kerberos]
    [sarnowski.kerberos.spnego :as spnego]))

(defn wrap-spnego
  "Wraps a SPNEGO inteceptor around the (Ring based web) app.
   jaas-config should contain JAAS login.conf information in the style of a map.
   If it's nil then the SPNEGO inteceptor isn't applied.
   Optional named arguments (e.g. exempt?) for the SPNEGO inteceptor are passed through opts."
  [app jaas-config & opts]
  (if (some? jaas-config)
    (apply spnego/authenticate 
      app 
      (kerberos/create-service-subject (:principal jaas-config) (kerberos/create-config jaas-config))
      opts)
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
  "This credential checker simply checks for evidence of pre-authentication: that a remote-user value exists.
   The remote-user value is converted to the Friend :identity value using the map-remote-user-fn.
   Its default definition is Clojure's identity function."
  ([get-roles-fn opts]
    (pre-authentication-credential-fn identity get-roles-fn opts))
  ([map-remote-user-fn get-roles-fn {:keys [remote-user]}]
    (when (some? remote-user)
      (info "Pre-authentication recognised for:" remote-user)
      (let [username (map-remote-user-fn remote-user)]
        {:identity username :roles (get-roles-fn username)}))))