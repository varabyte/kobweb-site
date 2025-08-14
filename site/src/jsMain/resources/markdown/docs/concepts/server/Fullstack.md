---
description: How to declare a full-stack Kobweb project and define API routes.
---

Kobweb not only helps you create frontend files for a website, but it also makes it easy to add backend functionality as
well.

Similar to how you define pages on the frontend, you can declare annotated API methods on the backend, at which point
Kobweb can discover them and handle all the boilerplate around setting up a web server for you.

All backend features also support Kobweb's live reloading experience, so you can iterate on your server while it is
running.

> [!QUESTION]
> Are you an experienced Ktor user who prefers to maintain full control over their backend? If so, consider reading the
> ${DocsLink("Using a Custom Backend", "/docs/guides/existing-backend")} guide as well, to understand your options.

## Declare a full-stack project

In your site's build script, make sure you call `configAsKobwebApplication(includeServer = true)`. Just with that done,
you are ready to write server logic for your Kobweb site.

A Kobweb project will always at least have a JavaScript target, representing the frontend, but if you declare an
intention to implement a server, that will create a JVM target for you as well. You can add dependencies to this
target if you want to make them available to your server code:

```kotlin 1,12,15-17 "site/build.gradle.kts"
import com.varabyte.kobweb.gradle.application.util.configAsKobwebApplication

plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.jetbrains.compose)
    alias(libs.plugins.kobweb.application)
}

/* ... */

kotlin {
    configAsKobwebApplication(includeServer = true)
    sourceSets {
        /* ... */
        jvmMain.dependencies {
            /* ... */
        }
    }
}
```

The easy way to check if everything is set up correctly is to open your project inside IntelliJ IDEA, wait for it to
finish indexing, and check that the `jvmMain` folder is detected as a module (if so, it will be given a special icon
and look the same as the `jsMain` folder):

![Kobweb JVM main set up correctly](https://github.com/varabyte/media/raw/main/kobweb/images/kobweb-jvm-main.png)

## <span id="api-routes">Define API routes</span>

You can define and annotate methods which will generate server endpoints you can interact with. To add one:

1. Define your method (optionally `suspend`able) in a file somewhere under the `api` package in your `jvmMain` source
   directory.
1. The method should take exactly one argument, an `ApiContext`.
1. Annotate it with `@Api`

For example, here's a simple method that echoes back an argument passed into it:

```kotlin "jvmMain/kotlin/com/mysite/api/Echo.kt"
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

```kotlin "jsMain/kotlin/com/mysite/pages/ApiDemo.kt"
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

## Respond to an API request

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
> The `ctx.res.setBodyText` method sets the status code to 200 automatically for you, which is why code in an earlier
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

## Intercept API routes

Kobweb provides a way to intercept all incoming API requests, getting a first chance to handle them before they get
passed to the actual API route handler.

To intercept all routes, declare a suspend method annotated with `@ApiInterceptor`. This method must take a
`ApiInterceptorContext` parameter and return a `Response`.

You can check the context parameter for the path associated with the incoming request. If you don't want to handle a
particular path, you can call `ctx.dispatcher.dispatch()` to pass it on as normal:

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
> `https://example.com/admin/dashboard` and then getting redirected to `https://example.com/login` instead.
>
> API interceptors also do not work for API streams (which will be discussed later in this article) since there's
> nothing general to intercept there. API streams connect once and then persist.

## Dynamic API routes

Similar to ${DocsLink("Dynamic routes", "../foundation/routing#dynamic-routes")}, you can define API routes using curly
braces in the same way to indicate a dynamic value that should be captured with some binding name.

For example, the following endpoint will capture the value "123" into a key name called
"article" when querying `articles/123`:

```kotlin "jvmMain/kotlin/com/mysite/api/articles/Article.kt"
@Api("{}")
suspend fun fetchArticle(ctx: ApiContext) {
    val articleId = ctx.req.params["article"] ?: return
    // ...
}
```

Recall from the `@Page` docs that specifying a name inside the curly braces defines the variable name used to capture
the value. When empty, as above, Kobweb uses the filename to generate it. In other words, you could explicitly specify
`@Api("{article}")` in the above example for the exact same effect.

Once this API endpoint is defined, query it as you would any normal API endpoint:

```kotlin "jsMain/kotlin/com/mysite/pages/articles/Article.kt"
coroutineScope.launch {
  // Will cause the "article" variable on the server
 // to get set to "123"
  val articleText = window.api.get("articles/123").decodeToString()
  // ...
}
```

## `@InitApi` methods and initializing services

A Kobweb server supports declaring methods that should be run when it starts up. These methods must be annotated with
`@InitApi` and must take a single `InitApiContext` parameter.

> [!IMPORTANT]
> If you are running a development server and change any of your backend code, causing a live reloading event, the
> init methods will be run again.

The `InitApiContext` class exposes a mutable set property (called `data`) which you can put anything into. Meanwhile,
`@Api` methods expose an immutable version of `data`. This allows you to initialize a service in an `@InitApi` method
and then access it in your `@Api` methods.

Let's demonstrate a concrete example. Imagine you have an interface called `Database` and a mutable
subclass `MutableDatabase` that implements it and provides additional APIs for mutating the database.

The skeleton for registering and later querying such a database instance might look like this:

```kotlin 1,2,10,16
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
  val db = ctx.data.getValue<Database>()
  ctx.res.setBodyText(db.query("SELECT * FROM users").toString())
}
```

## <span id="api-streams">Define API streams</span>

Kobweb servers support persistent connections via streams. Streams are essentially named channels that maintain
continuous contact between the client and the server, allowing either to send messages to the other at any time. This is
especially useful if you want your server to be able to communicate updates to your client without needing to poll.

Additionally, multiple clients can connect to the same stream. In this case, the server can choose to not only send a
message back to your client, but also to broadcast messages to all users (or a filtered subset of users) on the same
stream. You could use this, for example, to implement a chat server with rooms.

### Example API stream

Like API routes, API streams must be defined under the `api` package in your `jvmMain` source directory. By default, the
name of the stream will be derived from the file name and path that it is declared in (e.g. `api/lobby/Chat.kt` will
create a stream named "lobby/chat").

Unlike API routes, API streams are defined as properties, not methods. This is because API streams need to be a bit more
flexible than routes, since streams consist of multiple distinct events: client connection, client messages, and
client disconnection.

Also unlike API routes, streams do not have to be annotated. The Kobweb Application plugin can automatically detect
them.

For example, here's a simple stream, declared on the backend, that echoes back any argument it receives:

```kotlin "jvmMain/kotlin/com/mysite/api/Echo.kt"
val echo = object : ApiStream {
  override suspend fun onClientConnected(ctx: ClientConnectedContext) {
    // Optional: ctx.stream.broadcast a message to all other clients that a new stream connected
    // Optional: Update ctx.data here, initializing data associated with ctx.stream.id
  }
  override suspend fun onTextReceived(ctx: TextReceivedContext) {
    ctx.stream.send(ctx.text)
  }
  override suspend fun onClientDisconnected(ctx: ClientDisconnectedContext) {
    // Optional: ctx.stream.broadcast a message to all other clients that a stream disconnected
    // Optional: Update ctx.data here, removing data associated with ctx.stream.id
  }
}
```

To communicate with an API stream from your site, you need to create a stream connection on the client. We provide the
`rememberApiStream` method to help with this:

```kotlin "jsMain/kotlin/com/mysite/pages/ApiStreamDemo.kt"
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
you should see "Echoed: hello!" each time you press the button.

> [!TIP]
> Run `kobweb create examples/chat` to instantiate a project that uses API streams to implement a very simple chat
> application. Feel free to reference that project for a more realistic example.

### API stream conveniences

The above example was intentionally verbose, to showcase the broader functionality around API streams. However,
depending on your use-case, you can elide a fair bit of boilerplate.

First of all, the connect and disconnect handlers are optional, so you can omit them if you don't need them. Let's
simplify the echo example:

```kotlin "Backend"
val echo = object : ApiStream {
  override suspend fun onTextReceived(ctx: TextReceivedContext) {
    ctx.stream.send(ctx.text)
  }
}
```
```kotlin "Frontend"
val echoStream = rememberApiStream("echo", object : ApiStreamListener {
  override fun onTextReceived(ctx: TextReceivedContext) {
    console.log("Echoed: ${ctx.text}")
  }
})
```

Additionally, if you only care about the text event, there are convenience methods for that:

```kotlin "Backend"
val echo = ApiStream { ctx -> ctx.stream.send(ctx.text) }
```
```kotlin "Frontend"
val echoStream = rememberApiStream("echo") {
  ctx -> console.log("Echoed: ${ctx.text}")
}
```

In practice, your API streams will probably be a bit more involved than the echo example above, but it is nice to know
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

## API routes vs. API streams

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
routes are generally safe to use, so use them often.

However, if you have a situation where you need to communicate events in real-time, especially situations where you want
your client to be continuously directed what to do by the server via events, API streams are a great choice.

> [!NOTE]
> You can also search online about REST vs WebSockets, as these are the technologies that API routes and API streams are
> implemented with. Any discussions about them should apply here as well.

## Server logs

When you run `kobweb run`, the spun-up web server will, by default, log to the `.kobweb/server/logs` directory.

> [!NOTE]
> You can generate logs using the `ctx.logger` property provided to `@Api` calls.

You can configure logging behavior by editing the `.kobweb/conf.yaml` file. Below we show setting all parameters to
their default values:

```yaml ".kobweb/conf.yaml"
server:
  logging:
    level: DEBUG # ALL, TRACE, DEBUG, INFO, WARN, ERROR, OFF
    enableConsoleLogging: true # If false, logs will not be written to stdout/stderr
    enableFileLogging: true # If false, a log file will not be created
    logRoot: ".kobweb/server/logs"
    clearLogsOnStart: true # Warning - if true, wipes ALL files in logRoot, so don't put other files in there!
    logFileBaseName: "kobweb-server" # e.g. "kobweb-server.log", "kobweb-server.2023-04-13.log"
    maxFileCount: null # null = unbound. One log file is created per day, so 30 = 1 month of logs
    totalSizeCap: 10MiB # null = unbound. Accepted units: B, K, M, G, KB, MB, GB, KiB, MiB, GiB
    compressHistory: true # If true, old log files are compressed with gzip
```

The above defaults were chosen to be reasonable for most users running their projects on their local machines in
developer mode. However, for production servers, you may want to set `clearLogsOnStart` to false, bump up the
`totalSizeCap` after reviewing the disk limitations of your web server host, and maybe set `maxFileCount` to a reasonable
limit.

> [!NOTE]
> Most users might assume "10MB" is 10 * 1024 * 1024 bytes, but here it will actually result in 10 * 1000 * 1000 bytes.
> You probably want to use "KiB", "MiB", or "GiB" when you configure this value.

## CORS

[CORS](https://developer.mozilla.org/en-US/docs/Web/HTTP/CORS), or *Cross-Origin Resource Sharing*, is a security
feature built on the idea that a web page should not be able to make requests for resources from a server that is not
the same as the one that served the page *unless* it was served from a trusted domain.

To configure CORS for a Kobweb backend, Kobweb's `.kobweb/conf.yaml` file allows you to declare such trusted domains
using a `cors` block:

```yaml ".kobweb/conf.yaml"
server:
  cors:
    hosts:
      - name: "example.com"
        schemes:
          - "https"
```

> [!NOTE]
> Specifying the schemes is optional. If you don't specify them, Kobweb defaults to "http" and "https".

> [!NOTE]
> You can also specify subdomains, e.g.
> ```yaml
> - name: "example.com"
>   subdomains:
>     - "en"
>     - "de"
>     - "es"
> ```
> which would add CORS support for `en.example.com`, `de.example.com`, and `es.example.com`, as well as `example.com`
> itself.

Once configured, your Kobweb server will be able to respond to data requests from any of the specified hosts.

> [!TIP]
> If you find that your full-stack site, which was working locally during development, rejects requests in the
> production version, check your browser's console logs. If you see errors in there about a violated CORS policy, that
> means you didn't configure CORS correctly.
