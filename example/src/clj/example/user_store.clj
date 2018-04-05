;;   Copyright (c) 7theta. All rights reserved.
;;   The use and distribution terms for this software are covered by the
;;   Eclipse Public License 1.0 (http://www.eclipse.org/legal/epl-v10.html)
;;   which can be found in the LICENSE file at the root of this
;;   distribution.
;;
;;   By using this software in any fashion, you are agreeing to be bound by
;;   the terms of this license.
;;   You must not remove this notice, or any others, from this software.

(ns example.user-store
  (:require [re-frame-via.authenticator :as auth]
            [integrant.core :as ig]))

(defmethod ig/init-key :example/user-store [_ _]
  (fn [id]
    {:id id
     :password (auth/hash-password id)}))
