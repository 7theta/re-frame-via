# com.7theta/re-frame-via

[![Current Version](https://img.shields.io/clojars/v/com.7theta/re-frame-via.svg)](https://clojars.org/com.7theta/re-frame-via)
[![GitHub license](https://img.shields.io/github/license/7theta/re-frame-via.svg)](LICENSE)
[![Circle CI](https://circleci.com/gh/7theta/re-frame-via.svg?style=shield)](https://circleci.com/gh/7theta/re-frame-via)
[![Dependencies Status](https://jarkeeper.com/7theta/re-frame-via/status.svg)](https://jarkeeper.com/7theta/re-frame-via)

# Effect Handler

A [re-frame](https://github.com/Day8/re-frame) [Effect Handler](https://github.com/Day8/re-frame/tree/develop/docs)
that relies on [via](https://github.com/7theta/via) to provide WebSocket based messaging to the server.

## Registering the effects handler

The namespace where event handlers are registered, typically
`events.cljs`, is the generally the place where the effects handler
can be registered with re-frame.

In order to register the effects handler:

```clj
(ns app.events
  (:require
    ...
    [re-frame-via.fx :as via-fx]
    ...))

(via-fx/register <via-server-proxy>)
```

## Using the effects handler

Once the effects handler is registered it can be used within an event
handler as follows:

```clj
(reg-event-fx
  :some-event
  (fn [{:keys [db]} _]
    {:db  (assoc db :show-loading true)
     :via/dispatch {:message [:api/fetch-items {:filter {:color "red"}}]
                    :on-success [:api/fetch-items-succeeded]
                    :on-failure [:api/fetch-items-failed]}}))
```

The `:on-success` and `:on-failure` keys can be omitted for one-way
messages. Both keys must be provided or omitted, e.g., providing a
success handler without providing a failure handler is not supported.

Multiple messages can be sent by passing a sequence of maps to the
`:via/dispatch` key.

### Handlers for :on-success and :on-failure

Normal re-frame handlers are used for `:on-success` and
`:on-failure`. The event handlers will receive the response as the
second argument of the event vector.

```clj
(reg-event-db
  :api/fetch-items-succeeded
  (fn [db [_ result]]
    (assoc db :items result)}))
```

## Copyright and License

Copyright © 2018 7theta

Distributed under the Eclipse Public License.
