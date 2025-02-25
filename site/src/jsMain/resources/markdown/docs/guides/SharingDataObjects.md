---
title: Sharing Data Objects Between Frontend and Backend
follows: CustomFonts
---

If you start working on a fullstack project, you might find yourself experimenting during the prototype phase by passing
raw strings back and forth between your client and server. However, you'll likely eventually want to send rich data
objects instead, as they are more suited to encapsulating complexity while also providing type safety.

Thanks to Kotlin multiplatform and Kotlinx serialization, this is easy to do!

## Overview

In summary, you will need to:

* Add dependencies on `kotlinx.serialization` and (optionally) `kobwebx.serialization.kotlinx` to your site's build
  script.
* Create a `commonMain` folder which is where your data objects will live.
* Add backend and frontend code that serializes and deserializes these objects before sending them over the network.

To see this in action, we'll demonstrate implementing a single request / response object pair. To keep it simple, the
request will contain a string that should be sent to the server and echoed back to the client in the response object. To
make the request a little more interesting, we'll also include an operation instruction that the server should perform
on the string before sending it back.

> [!NOTE]
> If you're not familiar with the request / response pattern, it is a common way to structure an API for services that
> communicate over a network. When you stick with it, the convention is easy to read and understand quickly, and the
> approach is generally conducive to backwards compatibility when you inevitably need to add new fields later.
> (Writing backwards compatible data objects is outside the scope of this guide, however.)

## Build script

`gradle/libs.versions.toml`
```toml
[versions]
# Please specify desired versions in your project
kobweb = "..."
kotlin = "..."
kotlinx-serialization = "..."

[libraries]
kobwebx-serialization-kotlinx = { module = "com.varabyte.kobwebx:kobwebx-serialization-kotlinx", version.ref = "kobweb" }
kotlinx-serialization-json = { module = "org.jetbrains.kotlinx:kotlinx-serialization-json", version.ref = "kotlinx-serialization" }

[plugins]
kotlin-serialization = { id = "org.jetbrains.kotlin.plugin.serialization", version.ref = "kotlin" }
```

`site/build.gradle.kts`
```kotlin

plugins {
    alias(libs.plugins.kotlin.serialization)
}

group = "com.example"

kotlin {
    configAsKobwebApplication(includeServer = true)
    sourceSets {
        commonMain.dependencies {
            implementation(libs.kotlinx.serialization.json)
            implementation(libs.kobwebx.serialization.kotlinx)
        }
    }
}
```

## Common code

`site/src/commonMain/kotlin/com/example/models/Echo.kt`
```kotlin
package com.exmaple.models

import kotlinx.serialization.Serializable

enum class EchoOperation {
    AS_IS, // Example -> Example
    REVERSE, // Example -> elpmaxE
    LOWERCASE, // Example -> example
    UPPERCASE, // Example -> EXAMPLE
}

@Serializable
class EchoRequest(
    val text: String,
    val operation: EchoOperation = EchoOperation.AS_IS,
)

@Serializable
class EchoResponse(
    val text: String,
)
```

## Backend code

We'll be designing this API as a POST request, even though in this case it is stateless and using GET might more
reasonably be considered suitable. This is because, in practice, when you send request objects to a server, you commonly
want to add, update, or delete some state on the server.

But more importantly, HTTP post requests support including a body, which is the natural place to embed rich data
objects; GET requests do not. (We'll show how to handle GET requests as well shortly.)

`site/src/jvmMain/kotlin/com/example/api/Echo.kt`
```kotlin
package com.example.api

@Api
fun Echo(ctx: ApiContext) {
    if (ctx.req.method != HttpMethod.POST) return

    val echoRequest = ctx.req.readBody<EchoRequest>()!!
    val echoResponse = EchoResponse(
        text = when (echoRequest.operation) {
            EchoOperation.AS_IS -> echoRequest.text
            EchoOperation.REVERSE -> echoRequest.text.reversed()
            EchoOperation.LOWERCASE -> echoRequest.text.lowercase()
            EchoOperation.UPPERCASE -> echoRequest.text.uppercase()
        }
    )
    ctx.res.setBody(echoResponse)
}
```

Hopefully the endpoint is fairly self-explanatory. We parse the request body and, based on the operation, populate the
response body.

> [!NOTE]
> `Request.readBody` and `Response.setBody` are convenience methods that are provided by the
> `com.varabyte.kobwebx:kobwebx-serialization-kotlinx` artifact we included earlier.
> 
> You could also write `ctx.req.readBodyText()?.let { text -> Json.decodeFromString<EchoRequest>(text) }` and
> `ctx.res.setBodyText(Json.encodeToString(echoResponse))` if you don't mind the verbosity and want to skip adding the
> dependency.

If you really want to pass a data object with a GET request, you can use query parameters for that:
```kotlin
@Api
fun Echo(ctx: ApiContext) {
    if (ctx.req.method != HttpMethod.GET) return

    val echoRequest = Json.decodeFromString<EchoRequst>(
        ctx.req.params.getValue("data")
    )
    /* ... */
}
```

## Frontend code

With our endpoint defined, we can now trigger it from the frontend. In a `@Page` somewhere, you'll want to call one of
the `window.api` HTTP methods that can take and serialize a body argument.

As the `window.api` methods are all suspend functions, one of the easiest ways to call them (and thus demonstrate one)
is from a `LaunchedEffect` block:
```kotlin
LaunchedEffect(Unit) {
    val response = window.api.post<EchoRequest, EchoResponse>(
        "echo", body = EchoRequest("test", EchoOperation.REVERSE)
    )
    println("Got response: ${response.text}") // Got response: tset
}
```

That's it!

> [!NOTE]
> If you are getting compile errors with the above code, be sure to import the relevant extension method. For the above
> example, this would be `import com.varabyte.kobweb.browser.post`.

In practice, you'll probably use something like `rememberCoroutineScope` and use `scope.launch` in response to some user
event, such as clicking a button:
```kotlin
val scope = rememberCoroutineScope()

Button(onClick = {
    scope.launch {
        val response = ... // Same code as earlier
    }
}) {
    Text("Click me")
}
```

If you want to pass a data object along with a GET request, instead, you can embed it as a query parameter:
```kotlin
val response = window.api.get<EchoResponse>(
  "echo?data=${Json.encodeToString(EchoRequest("test"))}"
)
```

Characters will automatically be URL-encoded when you do this.

## Final thoughts

Thanks to Kotlin Multiplatform and Kotlinx serialization, it's easy to share data objects between the frontend and the
backend of a Kobweb fullstack project.

Historically, sharing data has been a pain point for fullstack developers. A common solution is to reach out to a
solution like [Protocol Buffers](https://protobuf.dev/), which is a way to declare data values in a language-agnostic
manner and then use a custom protobuf compiler to convert that declaration to target code you care about.

If you have to support a client and server that use different languages, then this is still a reasonable approach, but
if you can use Kotlin on both ends, then the approach discussed in this guide is much simpler.
