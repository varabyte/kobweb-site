---
follows: ReplaceWidgetStyles
---

Let's say you've decided on creating a full stack website using Kobweb. This section walks you through setting it up as
well as introducing the various APIs for communicating to the backend from the frontend.

### Declare a full stack project

A Kobweb project will always at least have a JavaScript component, but if you declare a JVM target, that will be used to
define custom server logic that can then be used by your Kobweb site.

It's easiest to let Kobweb do it for you. In your site's build script, make sure you've declared
`configAsKobwebApplication(includeServer = true)`:

```kotlin
// site/build.gradle.kts
import com.varabyte.kobweb.gradle.application.util.configAsKobwebApplication

plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.jetbrains.compose)
    alias(libs.plugins.kobweb.application)
}

/* ... */

kotlin {
    configAsKobwebApplication(includeServer = true)
    /* ... */
}
```

> [!IMPORTANT]
> `configAsKobwebApplication(includeServer = true)` declares and sets up both `js()` and `jvm()`
> [Kotlin Multiplatform targets](https://kotlinlang.org/docs/multiplatform-set-up-targets.html) for you. If you don't
> set `includeServer = true` explicitly, only the JS target will be declared.

The easy way to check if everything is set up correctly is to open your project inside IntelliJ IDEA, wait for it to
finish indexing, and check that the `jvmMain` folder is detected as a module (if so, it will be given a special icon
and look the same as the `jsMain` folder):

![Kobweb JVM main set up correctly](https://github.com/varabyte/media/raw/main/kobweb/images/kobweb-jvm-main.png)

### Define API routes

You can define and annotate methods which will generate server endpoints you can interact with. To add one:

1. Define your method (optionally `suspend`able) in a file somewhere under the `api` package in your `jvmMain` source
   directory.
1. The method should take exactly one argument, an `ApiContext`.
1. Annotate it with `@Api`

For example, here's a simple method that echoes back an argument passed into it:

```kotlin
// jvmMain/kotlin/com/mysite/api/Echo.kt

@Api
suspend fun echo(ctx: ApiContext) {
    // ctx.req is for the incoming request, ctx.res for responding back to the client

    // Params are parsed from the URL, e.g. here "/api/echo?message=..."
    val msg = ctx.req.params["message"] ?: ""
    ctx.res.setBodyText(msg)
}
```

After running your project, you can test the endpoint by visiting `mysite.com/api/echo?message=hello`

You can also trigger the endpoint in your frontend code by using the extension `api` property added to the
`kotlinx.browser.window` class:

```kotlin
@Page
@Composable
fun ApiDemoPage() {
  val coroutineScope = rememberCoroutineScope()

  Button(onClick = {
    coroutineScope.launch {
      println("Echoed: " + window.api.get("echo?message=hello").decodeToString())
    }
  }) { Text("Click me") }
}
```

All the HTTP methods are supported (`post`, `put`, etc.).

These methods will throw an exception if the request fails for any reason. Note that for every HTTP method, there's a
corresponding "try" version that will return null instead (`tryPost`, `tryPut`, etc.).

If you know what you're doing, you can of course always use [`window.fetch(...)`](https://developer.mozilla.org/en-US/docs/Web/API/fetch)
directly.

### Responding to an API request

When you define an API route, you are expected to set a status code for the response, or otherwise it will default to
status code `404`.

In other words, the following API route stub will return a 404:

```kotlin
@Api
suspend fun error404(ctx: ApiContext) {
}
```

In contrast, this minimal API route returns an OK status code:

```kotlin
@Api
suspend fun noActionButOk(ctx: ApiContext) {
    ctx.res.status = 200
}
```

> [!IMPORTANT]
> The `ctx.res.setBodyText` method sets the status code to 200 automatically for you, which is why code in the previous
> section worked without setting the status directly. Of course, if you wanted to return a different status code value
> after setting the body text, you could explicitly set it right after making the `setBodyText` call. For example:
> ```kotlin
> ctx.res.setBodyText("...")
> ctx.res.status = 201
> ```

The design for defaulting to 404 was chosen to allow you to conditionally handle API routes based on input conditions,
where early aborts automatically result in the client getting an error.

A very common case is creating an API route that only handles POST requests:

```kotlin
@Api
suspend fun updateUser(ctx: ApiContext) {
    if (ctx.req.method != HttpMethod.POST) return
    // ...
    ctx.res.status = 200
}
```

Finally, note that you can add headers to your response. A common endpoint that some servers provide is a redirect (302)
with an updated URL location. This would look like:

```kotlin
@Api
suspend fun redirect(ctx: ApiContext) {
    if (ctx.req.method != HttpMethod.GET) return
    ctx.res.headers["Location"] = "..."
    ctx.res.status = 302
  
  // Note: A convenience method is provided for this:
  // `ctx.res.setAsRedirect("...", 302)
}
```

Simple!

### Intercepting API routes

Kobweb provides a way to intercept all incoming API requests, getting a first chance to handle them before they get
passed to the actual API route handler.

To intercept all routes, declare a suspend method annotated with `@ApiInterceptor`. This method must take a
`ApiInterceptorContext` parameter and return a `Response`:

```kotlin
@ApiInterceptor
suspend fun interceptRequest(ctx: ApiInterceptorContext): Response {
    return when {
        ctx.path == "/example" -> Response().apply { setBodyText("Intercepted!") }
        // Default: pass request to the route it is normally handled by
        else -> ctx.dispatcher.dispatch()
    }
}
```

The `ApiInterceptorContext` class provides access to a mutable version of the incoming request, which gives you a chance
to modify it first (e.g. adding cookies, updating headers) before it gets handled, which can be useful.

The `ctx.dispatcher.dispatch` method takes an optional path you can specify so that you can delegate a request to a
different API route:

```kotlin
@ApiInterceptor
suspend fun interceptRequest(ctx: ApiInterceptorContext): Response {
    return when {
        // User will think "/legacy" handled the request;
        // actually, "/new" did
        ctx.path == "/legacy" -> ctx.dispatcher.dispatch("/new")
        else -> ctx.dispatcher.dispatch()
    }
}
```

Perhaps you aren't interested in interfering with any incoming requests, but you want to modify all responses before
they get sent back to the client. You can use this pattern for that:

```kotlin
@ApiInterceptor
suspend fun interceptResponse(ctx: ApiInterceptorContext): Response {
    return ctx.dispatcher.dispatch().also { res ->
        res.headers["X-Intercepted"] = "true"
    }
}
```

> [!CAUTION]
> API interceptors only work for API routes, not static files or other resources. In other words, although you *can* use
> an interceptor to intercept a call to "/api/users/edit", the feature is not designed to handle a user navigating to
> "/admin/dashboard" and then getting redirected to "/login" instead.
>
> Navigation logic has to be handled client-side, because Kobweb apps are single-page applications, meaning all pages
> are downloaded from the server at the same time. This should be fine in practice -- the `/admin/dashboard` page should
> not contain any secrets itself, but the data it tries to download from the server (via an API call) is the part that
> should be protected.
>
> API interceptors also do not work for API streams, since there's nothing general to intercept there. API streams are
> set up once and then persisted.

### Dynamic API routes

Similar to [dynamic `@Page` routes](#dynamic-routes), you can define API routes using curly braces in the same way to
indicate a dynamic value that should be captured with some binding name.

For example, the following endpoint will capture the value "123" into a key name called
"article" when querying `articles/123`:

```kotlin
// jvmMain/kotlin/com/mysite/api/articles/Article.kt

@Api("{}")
suspend fun fetchArticle(ctx: ApiContext) {
    val articleId = ctx.req.params["article"] ?: return
    // ...
}
```

Recall from the `@Page` docs that specifying a name inside the curly braces defines the variable name used to capture
the value. When empty, as above, Kobweb uses the filename to generate it. In other words, you could explicitly specify
`@Api("{article}")` for the same effect.

Once this API endpoint is defined, you just query it as you would any normal API endpoint:

```kotlin
coroutineScope.launch {
  val articleText = window.api.get("articles/123").decodeToString()
  // ...
}
```

Finally, astute readers might notice that (like dynamic `@Page` routes) we use the same property to query dynamic route
values as well as query parameters.

Captured dynamic values will always take precedence over query parameters in the `params` map. In practice, this should
never be a problem, because it would be very confusing design to write an API endpoint that got called like
`articles/123?article=456`. That said, you can also use `ctx.req.queryParams["article"]` to disambiguate this case if
necessary.

### `@InitApi` methods and initializing services

Kobweb also supports declaring methods that should be run when your server starts up, which is particularly useful for
initializing services that your `@Api` methods can then use. These methods must be annotated with `@InitApi` and must
take a single `InitApiContext` parameter.

> [!IMPORTANT]
> If you are running a development server and change any of your backend code, causing a live reloading event, the
> init methods will be run again.

The `InitApiContext` class exposes a mutable set property (called `data`) which you can put anything into. Meanwhile,
`@Api` methods expose an immutable version of `data`. This allows you to initialize a service in an `@InitApi` method
and then access it in your `@Api` methods.

Let's demonstrate a concrete example, imagining we had an interface called `Database` with a mutable
subclass `MutableDatabase` that implements it and provides additional APIs for mutating the database.

The skeleton for registering and later querying such a database instance might look like this:

```kotlin
@InitApi
fun initDatabase(ctx: InitApiContext) {
  val db = MutableDatabase()
  db.createTable("users", listOf("id", "name")).apply {
    addRow(listOf("1", "Alice"))
    addRow(listOf("2", "Bob"))
  }
  db.loadResource("products.csv")

  ctx.data.add<Database>(db)
}

@Api
fun getUsers(ctx: ApiContext) {
  if (ctx.req.method != HttpMethod.GET) return
  val db = ctx.data.get<Database>()
  ctx.res.setBodyText(db.query("SELECT * FROM users").toString())
}
```

### <span id="api-stream">Define API streams</span>

Kobweb servers also support persistent connections via streams. Streams are essentially named channels that maintain
continuous contact between the client and the server, allowing either to send messages to the other at any time. This is
especially useful if you want your server to be able to communicate updates to your client without needing to poll.

Additionally, multiple clients can connect to the same stream. In this case, the server can choose to not only send a
message back to your client, but also to broadcast messages to all users (or a filtered subset of users) on the same
stream. You could use this, for example, to implement a chat server with rooms.

#### Example API stream

Like API routes, API streams must be defined under the `api` package in your `jvmMain` source directory. By default, the
name of the stream will be derived from the file name and path that it's declared in (e.g. "api/lobby/Chat.kt" will
create a channel named "lobby/chat").

Unlike API routes, API streams are defined as properties, not methods. This is because API streams need to be a bit more
flexible than routes, since streams consist of multiple distinct events: client connection, client messages, and
client disconnection.

Also unlike API routes, streams do not have to be annotated. The Kobweb Application plugin can automatically detect
them.

For example, here's a simple stream, declared on the backend, that echoes back any argument it receives:

```kotlin
// jvmMain/kotlin/com/mysite/api/Echo.kt

val echo = object : ApiStream {
  override suspend fun onClientConnected(ctx: ClientConnectedContext) {
    // Optional: ctx.stream.broadcast a message to all other clients that ctx.clientId connected
    // Optional: Update ctx.data here, initializing data associated with ctx.clientId
  }
  override suspend fun onTextReceived(ctx: TextReceivedContext) {
    ctx.stream.send(ctx.text)
  }
  override suspend fun onClientDisconnected(ctx: ClientDisconnectedContext) {
    // Optional: ctx.stream.broadcast a message to all other clients that ctx.clientId disconnected
    // Optional: Update ctx.data here, removing data associated with ctx.clientId
  }
}
```

To communicate with an API stream from your site, you need to create a stream connection on the client:

```kotlin
@Page
@Composable
fun ApiStreamDemoPage() {
  val echoStream = rememberApiStream("echo", object : ApiStreamListener {
    override fun onConnected(ctx: ConnectedContext) {}
    override fun onTextReceived(ctx: TextReceivedContext) {
      console.log("Echoed: ${ctx.text}")
    }
    override fun onDisconnected(ctx: DisconnectedContext) {}
  })

  Button(onClick = {
    echoStream.send("hello!")
  }) { Text("Click me") }
}
```

After running your project, you can click on the button and check the console logs. If everything is working properly,
you should see "Echoed: hello!" for each time you press the button.

> [!TIP]
> The `examples/chat` template project uses API streams to implement a very simple chat application, so you can
> reference that project for a more realistic example.

#### API stream conveniences

The above example was intentionally verbose, to showcase the broader functionality around API streams. However,
depending on your use-case, you can elide a fair bit of boilerplate.

First of all, the connect and disconnect handlers are optional, so you can omit them if you don't need them. Let's
simplify the echo example:

```kotlin
// Backend
val echo = object : ApiStream {
  override suspend fun onTextReceived(ctx: TextReceivedContext) {
    ctx.stream.send(ctx.text)
  }
}

// Frontend
val echoStream = rememberApiStream("echo", object : ApiStreamListener {
  override fun onTextReceived(ctx: TextReceivedContext) {
    console.log("Echoed: ${ctx.text}")
  }
})
```

Additionally, if you only care about the text event, there are convenience methods for that:

```kotlin
// Backend
val echo = ApiStream { ctx -> ctx.stream.send(ctx.text) }

// Frontend
val echoStream = rememberApiStream("echo") {
  ctx -> console.log("Echoed: ${ctx.text}")
}
```

In practice, your API streams will probably be a bit more involved than the echo example above, but it's nice to know
that you can handle some cases only needing a one-liner on the server and another on the client to create a persistent
client-server connection!

> [!NOTE]
> If you need to create an API stream with stricter control around when it actually connects to the server, you can
> create the `ApiStream` object directly instead of using `rememberApiStream`:
> ```kotlin
> val echoStream = remember { ApiStream("echo") }
> val scope = rememberCoroutineScope()
>
> // Later, perhaps after a button is clicked...
> scope.launch {
>   echoStream.connect(object : ApiStreamListener { /* ... */ })
> }
> ```

### API routes vs. API streams

When faced with a choice, use API routes as often as you can. They are conceptually simpler, and you can query API
endpoints with a CLI program like curl and sometimes even visit the URL directly in your browser. They are great for
handling queries of or updates to server resources in response to user-driven actions (like visiting a page or clicking
on a button). Every operation you perform returns a clear response code in addition to some payload information.

Meanwhile, API streams are very flexible and can be a natural choice to handle high-frequency communication. But they
are also more complex. Unlike a simple request / response pattern, you are instead opting in to manage a potentially
long lifetime during which you can receive any number of events. You may have to concern yourself about interactions
between all the clients on the stream as well. API streams are fundamentally stateful.

You often need to make a lot of decisions when using API streams. What should you do if a client or server disconnects
earlier than expected? How do you want to communicate to the client that their last action succeeded or failed (and you
need to be clear about exactly which action because they might have sent another one in the meantime)? What structure do
you want to enforce, if any, between a client and server connection where both sides can send messages to each other at
any time?

Most importantly, API streams may not horizontally scale as well as API routes. At some point, you may find yourself in
a situation where a new web server is spun up to handle some intense load.

If you're using API routes, you're already probably delegating to a database service as your data backend, so this may
just work seamlessly.

But for API streams, you many naturally find yourself writing a bunch of broadcasting code. However, this only works to
communicate between all clients that are connected to the same server. Two clients connected to the same stream on
different servers are effectively in different, disconnected worlds.

The above situation is often handled by using a pubsub service (like Redis). This feels somewhat equivalent to using a
database as a service in the API route situation, but this code might not be as straightforward to migrate.

API routes and API streams are not a you-must-use-one-or-the-other situation. Your project can use both! In general, try
to imagine the case where a new server might get spun up, and design your code to handle that situation gracefully. API
routes are generally safe to use, so use them often. However, if you have a situation where you need to communicate
events in real-time, especially situations where you want your client to be continuously directed what to do by the
server via events, API streams are a great choice.

> [!NOTE]
> You can also search online about REST vs WebSockets, as these are the technologies that API routes and API streams are
> implemented with. Any discussions about them should apply here as well.
