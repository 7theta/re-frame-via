;;   Copyright (c) 7theta. All rights reserved.
;;   The use and distribution terms for this software are covered by the
;;   Eclipse Public License 1.0 (http://www.eclipse.org/legal/epl-v10.html)
;;   which can be found in the LICENSE file at the root of this
;;   distribution.
;;
;;   By using this software in any fashion, you are agreeing to be bound by
;;   the terms of this license.
;;   You must not remove this notice, or any others, from this software.

(ns example.config
  (:require [taoensso.sente.server-adapters.http-kit :refer [get-sch-adapter]]
            [integrant.core :as ig]))

(def config
  {:via.server/client-proxy
   {:sente-web-server-adapter (get-sch-adapter)
    :user-id-fn (fn [ring-req] (-> ring-req :params :user-id))}

   :via.server/router
   {:msg-handler (ig/ref :re-frame-via/authenticated-msg-handler)
    :client-proxy (ig/ref :via.server/client-proxy)}

   :re-frame-via/authenticated-msg-handler
   {:authenticator (ig/ref [:re-frame-via/authenticator])
    :un-authenticated-message-set #{:api.example/login}
    :msg-handler (ig/ref :example/msg-handler)}

   :example/msg-handler
   {:authenticator (ig/ref [:re-frame-via/authenticator])}

   :re-frame-via/authenticator
   {:query-fn (ig/ref [:example/user-store])}

   :example/user-store
   nil})

(ig/load-namespaces config)
