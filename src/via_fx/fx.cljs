(ns via-fx.fx
  (:require [via.client.server-proxy :as via]
            [re-frame.core :refer [reg-fx dispatch]]))

(declare send!)

;;; Public

(defn register
  "Registers an effects handler that will send requests to the server
  referred to by 'server-proxy' using via.

  The requests can be provided as a sequence or a single map of the
  following form:

    {:message [<server-message-keyword> <optional-parameter>]
     :on-success <re-frame event to dispatch on success>
     :on-error <re-frame event to dispatch on error>
     :timeout <optional timeout in ms>}

  The :on-success and :on-error events can be omitted for one-way
  messages to the server. However if a response from the server is
  desired, both must be provided"
  [server-proxy]
  (reg-fx
   :via
   (fn [request-map-or-seq]
     (doseq [request (if (sequential? request-map-or-seq)
                       request-map-or-seq
                       [request-map-or-seq])]
       (send! server-proxy request)))))

;;; Implementation

(defn- send!
  [server-proxy {:keys [message on-success on-failure timeout]}]
  (via/send! server-proxy message
             :timeout timeout
             :callback (when (and on-success on-failure)
                         (fn [reply]
                           (if (via/success? reply)
                             (dispatch (conj on-success reply))
                             (dispatch (conj on-failure reply)))))))
