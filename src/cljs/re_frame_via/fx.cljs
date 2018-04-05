;;   Copyright (c) 7theta. All rights reserved.
;;   The use and distribution terms for this software are covered by the
;;   Eclipse Public License 1.0 (http://www.eclipse.org/legal/epl-v10.html)
;;   which can be found in the LICENSE file at the root of this
;;   distribution.
;;
;;   By using this software in any fashion, you are agreeing to be bound by
;;   the terms of this license.
;;   You must not remove this notice, or any others, from this software.

(ns re-frame-via.fx
  (:refer-clojure :exclude [reset!])
  (:require [via.client.server-proxy :as via]
            [re-frame.core :refer [reg-fx dispatch]]
            [integrant.core :as ig]))

(declare send! reset!)

;;; Public

(reg-fx
 :via/reset
 (fn [{:keys [app config connection-params timeout] :as configuration}]
   (reset! app config connection-params :timeout timeout)))

(defn register
  "Registers an effects handler that will send requests to the server
  referred to by `server-proxy` using via. An optional `timeout` can
  be provided which will be used for requests if no :timeout is
  provided in the individual request.

  The requests can be provided as a sequence or a single map of the
  following form:

    {:message [<server-message-keyword> <optional-parameter>]
     :on-success <re-frame event to dispatch on success>
     :on-error <re-frame event to dispatch on error>
     :timeout <optional timeout in ms>}

  The :on-success and :on-error events can be omitted for one-way
  messages to the server. However if a response from the server is
  expected, both must be provided. Additionally all requests that
  expect a response from the server must have a timeout, which can
  be provided when the effects handler is registered and overridden
  in an individual request."
  [server-proxy & {:keys [timeout]}]
  (reg-fx
   :via/dispatch
   (fn [request-map-or-seq]
     (doseq [request (if (sequential? request-map-or-seq)
                       request-map-or-seq
                       [request-map-or-seq])]
       (send! server-proxy (cond-> request
                             (not (:timeout request)) (assoc :timeout timeout)))))))

(defn reset!
  "Reset the connection to the server. The `app` must be an atom,
  which will be re-initialized based on the `config` and the
  `connection-params`. This is typically used to attach authentication
  data to the server-proxy.

  The via fx handler will be re-registered after the connection is reset"
  [app config connection-params & {:keys [timeout]
                                   :or {timeout 5000}}]
  (swap! app
         #(do
            (ig/halt! %)
            (ig/init (cond-> config
                       connection-params (assoc :via.client/server-proxy connection-params)))))
  (register (:via.client/server-proxy @app) :timeout timeout))

;;; Implementation

(defn- send!
  [server-proxy {:keys [message on-success on-failure timeout]}]
  {:pre [(or (every? nil? [on-success on-failure])
             (every? identity [on-success on-failure timeout]))]}
  (if (and on-success on-failure)
    (via/send! server-proxy message
               :timeout timeout
               :callback (fn [reply]
                           (if (via/success? reply)
                             (dispatch (conj on-success reply))
                             (dispatch (conj on-failure reply)))))
    (via/send! server-proxy message)))
