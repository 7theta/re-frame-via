;;   Copyright (c) 7theta. All rights reserved.
;;   The use and distribution terms for this software are covered by the
;;   Eclipse Public License 1.0 (http://www.eclipse.org/legal/epl-v10.html)
;;   which can be found in the LICENSE file at the root of this
;;   distribution.
;;
;;   By using this software in any fashion, you are agreeing to be bound by
;;   the terms of this license.
;;   You must not remove this notice, or any others, from this software.

(ns re-frame-via.authenticator
  (:require [buddy.hashers :as bh]
            [buddy.sign.jwt :as jwt]
            [buddy.core.nonce :as bn]
            [clj-time.core :as t]
            [integrant.core :as ig]))

;; Initializes the authenticator with a 'query-fn' and an optional 'secret'.
;; The 'query-fn' must take an id for the user and return a hash map containing
;; a ':id' and ':password' (hashed) or a nil.
;; If a secret is not provided, a random secret is generated on initialization.

(defmethod ig/init-key :re-frame-via/authenticator [_ {:keys [query-fn secret]
                                                       :or {secret (bn/random-bytes 32)}}]
  {:query-fn query-fn :secret secret})

(defn create-token
  "Authenticates the user identified by `id` and `password` and returns a hash map
  with `:id` and `:token` (JWT token) if the authentication is successful. A nil
  is returned if the authentication fails."
  [{:keys [query-fn secret] :as authenticator} id password & {:keys [expiry] :or {expiry 24}}]
  (try
    (when-let [user (query-fn id)]
      (when (bh/check password (:password user))
        (let [user (dissoc user :password)]
          (assoc user :token (jwt/encrypt (assoc user :exp (t/plus (t/now) (t/hours expiry)))
                                          secret)))))
    (catch Exception _ nil)))

(defn validate-token
  "Validates the `token` using `authenticator`"
  [{:keys [secret] :as authenticator} token]
  (when token (jwt/decrypt token secret)))

(defn hash-password
  "Hashes `password` using the default algorithm (currently :bcrypt+sha512)"
  [password]
  (bh/derive password))
