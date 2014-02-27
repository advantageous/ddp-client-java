[![Build Status](https://travis-ci.org/sailorgeoffrey/ddp-client-java.png?branch=master)](https://travis-ci.org/sailorgeoffrey/ddp-client-java)
#Meteor DDP Client for Java

##Components

This library can be described by breaking it into three main parts.

 * Common Message Components
 * Subscription Adapters
 * RPC Client

The package structure represents this grouping as well.

###DDPMessageEndpoint
The message endpont is the websocket endpoint for all the DDP messages.  The endpoint is responsible for handling the
connection to the Meteor server and dispatching DDP messages to listeners.  Listeners are registered with the
registerHandler method.

The constructor takes two arguments: a javax.websocket.WebsocketContainer, and a MessageConverter.

```java
        DDPMessageEndpoint endpoint = new DDPMessageEndpointImpl(webSocketContainer, messageConverter);

        endpoint.registerHandler(ConnectedMessage.class, message ->
                System.out.println("connected to server! session: " + message.getSession()));
```

###MessageConverter
The message converter is what converts a DDP message into a strongly typed Java message object.  The only included
implementation is a JSONMessageConverter because the Meteor server uses JSON over websocket by default.  The reason this
was abstracted out of the main client is because one could potentially use another serialization strategy here.
(Google's Protocol Buffers, for example)

###SubscriptionAdapter
A subscription adapter is responsible for subscribing and unsubscribing from collections and handling subscription
messages.  This library includes a MapSubscription adapter that updates a java.util.Map from subscription messages.  The
map is injected into the MapSubscriptionAdapter so that you can use a simple HashMap or any map provider. (Memcached,
JGroups, etc.)

You can also extend the BaseSubscriptionAdapter to use any other local storage.  (minimongo anyone?)

```java
        Map<String, Map<String, Object>> dataMap = new HashMap<>();

        new MapSubscriptionAdapter(
                endpoint,
                new Subscription[]{
                        new Subscription("myCollection")
                },
                new JsonObjectConverter(),
                dataMap
        );
```

###ObjectConverter
The object convert is what converts Added and Changed DDP messages into their mapped Java objects.  The only included
converter is a JSON converter, but an EJSON converter would be handy in the future.

###RPCClient
The RPC client is used to call Meteor.method functions.  The call requires callbacks for success and failure.
