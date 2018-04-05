;;   Copyright (c) 7theta. All rights reserved.
;;   The use and distribution terms for this software are covered by the
;;   Eclipse Public License 1.0 (http://www.eclipse.org/legal/epl-v10.html)
;;   which can be found in the LICENSE file at the root of this
;;   distribution.
;;
;;   By using this software in any fashion, you are agreeing to be bound by
;;   the terms of this license.
;;   You must not remove this notice, or any others, from this software.

(ns example.handler
  (:require [via.defaults :refer [default-sente-endpoint]]
            [compojure.core :refer [GET POST routes]]
            [compojure.route :refer [resources]]
            [ring.util.response :refer [resource-response content-type]]
            [ring.middleware.defaults :refer [wrap-defaults site-defaults]]
            [ring.middleware.anti-forgery :refer [wrap-anti-forgery]]
            [ring.middleware.session :refer [wrap-session]]))

(defn handler
  [client-proxy]
  (let [ring-ajax-get-or-ws-handshake (:ring-ajax-get-or-ws-handshake-fn client-proxy)
        ring-ajax-post (:ring-ajax-post-fn client-proxy)
        routes (routes
                (GET "/" req-req (content-type
                                  (resource-response "public/index.html")
                                  "text/html"))
                (GET  default-sente-endpoint ring-req (ring-ajax-get-or-ws-handshake ring-req))
                (POST default-sente-endpoint ring-req (ring-ajax-post ring-req))
                (resources "/"))]
    (-> routes
        (wrap-defaults site-defaults)
        wrap-anti-forgery
        wrap-session)))
