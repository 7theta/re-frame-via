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
  (:require [re-frame.core :refer [dispatch]]))

(defmulti msg-handler :id)

;; Web Socket Life cycle

(defmethod msg-handler :chsk/state
  [{:as ev-msg :keys [?data]}]
  (let [[old-state new-state] ?data]
    (if (:open? new-state)
      (dispatch [:ws/connected])
      (dispatch [:ws/disconnected]))))

(defmethod msg-handler :chsk/recv
  [{:as ev-msg :keys [?data]}]
  (when-not (= :chsk/ws-ping (first ?data))
    (dispatch ?data)))

(defmethod msg-handler :chsk/handshake
  [{:as ev-msg :keys [?data]}]
  (let [[?uid ?csrf-token ?handshake-data] ?data]
    (js/console.info "Handshake: " (pr-str ?data))))

;; API

(defmethod msg-handler :default
  [{:as ev-msg :keys [event]}]
  (js/console.info "Unhandled event: " (str event)))
