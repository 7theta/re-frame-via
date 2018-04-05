;;   Copyright (c) 7theta. All rights reserved.
;;   The use and distribution terms for this software are covered by the
;;   Eclipse Public License 1.0 (http://www.eclipse.org/legal/epl-v10.html)
;;   which can be found in the LICENSE file at the root of this
;;   distribution.
;;
;;   By using this software in any fashion, you are agreeing to be bound by
;;   the terms of this license.
;;   You must not remove this notice, or any others, from this software.

(ns example.events
  (:require [example.app :refer [app]]
            [example.config :refer [config]]
            [re-frame-via.fx :as via-fx]
            [example.app :refer [app]]
            [example.db :as db]
            [re-frame.core :refer [reg-event-db reg-event-fx]]))

(via-fx/register (:via.client/server-proxy @app) :timeout 5000)

(reg-event-db
 :initialize-db
 (fn [_ _]
   db/default-db))

(reg-event-fx
 :ws/connected
 (fn [{:keys [db]} _]
   (js/console.info (if (:authenticated db)
                      "Authenticated WebSocket connected"
                      "WebSocket connected"))))

(reg-event-db
 :ws/disconnected
 (fn [db _]
   (js/console.info "WebSocket disconnected")
   {:server {:connected? false}}))

(reg-event-fx
 :ws/reset-connecton!
 (fn [_ [_ params]]
   {:via/reset {:app app
                :config config
                :connection-params params}}))

(reg-event-fx
 :login
 (fn [{:keys [db]} _]
   {:via/dispatch {:message [:api.example/login {:id "admin" :password "admin"}]
                   :on-success [:login/response]
                   :on-failure [:login/failed]}}))

(reg-event-fx
 :login/response
 (fn [{:keys [db]} [_ {:keys [token] :as login-creds}]]
   (when token
     {:dispatch [:login/succeeded login-creds]})))

(reg-event-fx
 :login/succeeded
 (fn [{:keys [db]} [_ login-creds]]
   {:db (assoc db :authenticated login-creds)
    :dispatch [:ws/reset-connecton! {:params {:token (:token login-creds)
                                              :user-id (:id login-creds)}}]}))

(reg-event-db
 :login/failed
 (fn [db error]
   (js/console.error ":login/failed" (pr-str error))
   (dissoc db :authenticated)))

(reg-event-fx
 :logout
 (fn [_ _]
   {:db db/default-db
    :dispatch [:ws/reset-connecton! nil]}))
