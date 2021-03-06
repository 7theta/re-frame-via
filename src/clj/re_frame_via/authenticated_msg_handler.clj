;;   Copyright (c) 7theta. All rights reserved.
;;   The use and distribution terms for this software are covered by the
;;   Eclipse Public License 1.0 (http://www.eclipse.org/legal/epl-v10.html)
;;   which can be found in the LICENSE file at the root of this
;;   distribution.
;;
;;   By using this software in any fashion, you are agreeing to be bound by
;;   the terms of this license.
;;   You must not remove this notice, or any others, from this software.

(ns re-frame-via.authenticated-msg-handler
  (:require [re-frame-via.authenticator :as auth]
            [integrant.core :as ig]))

(defmethod ig/init-key :re-frame-via/authenticated-msg-handler
  [_ {:keys [authenticator un-authenticated-message-set msg-handler]}]
  (fn [{:keys [id ?reply-fn ring-req] :as message}]
    (if (or (get un-authenticated-message-set id)
            (try (auth/validate-token authenticator (-> ring-req :params :token)) (catch Exception _ nil)))
      (msg-handler message)
      (cond-> {:re-frame-via/authenticated-msg-handler {:status :error :type :invalid-auth-token}}
        ?reply-fn ?reply-fn))))
