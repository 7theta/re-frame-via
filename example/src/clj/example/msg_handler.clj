;;   Copyright (c) 7theta. All rights reserved.
;;   The use and distribution terms for this software are covered by the
;;   Eclipse Public License 1.0 (http://www.eclipse.org/legal/epl-v10.html)
;;   which can be found in the LICENSE file at the root of this
;;   distribution.
;;
;;   By using this software in any fashion, you are agreeing to be bound by
;;   the terms of this license.
;;   You must not remove this notice, or any others, from this software.

(ns example.msg-handler
  (:require [re-frame-via.authenticator :as auth]
            [integrant.core :as ig]))

(defmulti msg-handler (fn [opts message] (:id message)))

(defmethod ig/init-key :example/msg-handler
  [_ {:keys [authenticator] :as opts}]
  (fn [message] (msg-handler opts message)))


(defmethod msg-handler :api.example/login
  [{:keys [authenticator] :as opts} {:keys [?data ?reply-fn]}]
  (when ?reply-fn
    (?reply-fn (auth/create-token authenticator (:id ?data) (:password ?data)))))

(defmethod msg-handler :default
  [_ {:keys [event ?reply-fn]}]
  (when ?reply-fn (?reply-fn {:via/unhandled-event-echoed-from-the-server event})))
