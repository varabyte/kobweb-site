---
description: How to define a web worker enhanced by Kobweb.
follows: Markdown
---

[Web workers](https://developer.mozilla.org/en-US/docs/Web/API/Web_Workers_API) are a standard web technology that allow
you to run JavaScript code in a separate thread from your main application. Although JavaScript is famously
single-threaded, web workers offer a way for you to run potentially expensive code in parallel to your main site without
slowing it down.

A web worker script is entirely isolated from your main site and has no access to the DOM. The only way to communicate
between them is via message passing.

> [!NOTE]
> Astute readers may recognize the [actor model](https://en.wikipedia.org/wiki/Actor_model) here, which is an effective
> way to allow concurrency without worrying about common synchronization issues that plague common lock-based
> approaches.

A somewhat forced but easy-to-understand example of a web worker is one that computes the first N prime numbers.

While the worker is crunching away on intensive calculations, your site still works as normal, fully responsive. When
the worker is finished, it posts a message to the application, which handles it by updating relevant UI elements.

## Web workers wrapped in Kobweb

Kobweb aims to make using web workers as easy as possible, while adding a layer of type safety on top of it.

Here's everything you have to do (we'll show concrete examples of these steps below):

* Create a new module and apply the Kobweb Worker Gradle plugin on it.
* Tag the `kotlin { ... }` block in your build script with a `configAsKobwebWorker()` call.
    * (Optional but recommended) Specify a name for your worker. Otherwise, the generic name "worker" (with a short random
      suffix) will be used, which is functional but may make it harder to debug if something goes wrong.
* Declare a dependency on `"com.varabyte.kobweb:kobweb-worker"`.
* Implement the `WorkerFactory` interface, providing a `WorkerStrategy` that represents the core logic of your worker.

### Build script

```kotlin
import com.varabyte.kobweb.gradle.worker.util.configAsKobwebWorker

plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.kobweb.worker) // or id("com.varabyte.kobweb.worker")
}

group = "example.worker"
version = "1.0-SNAPSHOT"

kotlin {
    configAsKobwebWorker("example-worker")
    sourceSets {
        jsMain.dependencies {
            implementation(libs.kobweb.worker) // or "com.varabyte.kobweb:kobweb-worker"
        }
    }
}
```

### Worker factory

The `WorkerFactory` interface is minimal:

```kotlin
interface WorkerFactory<I, O> {
  fun createStrategy(postOutput: OutputDispatcher<O>): WorkerStrategy<I>
  fun createIOSerializer(): IOSerializer<I, O>
}
```

This concise interface still captures a lot of information. Your implementation will specify:

* What types your worker accepts as input and output messages.
* How it serializes those input and output messages.
* logic for handling input messages from the application (through your `WorkerStrategy` implementation).
* logic for sending messages back to the application (via the `postOutput` object).

### Worker strategy

The `WorkerStrategy` class allows you to define what your worker does with input received from the application. It also
exposes a `self` property that provides standard library worker functionality via the
[ `DedicatedWorkerGlobalScope`](https://developer.mozilla.org/en-US/docs/Web/API/DedicatedWorkerGlobalScope) object.

```kotlin
abstract class WorkerStrategy<I> {
  protected val self: DedicatedWorkerGlobalScope
  abstract fun onInput(inputMessage: InputMessage<I>)
}
```

### Output dispatcher

`OutputDispatcher` is a simple class which allows you to send output messages back to the application.

```kotlin
class OutputDispatcher<O> {
  operator fun invoke(output: O, transferables: Transferables = Transferables.Empty)
}

// Note the invoke operator here, so you can treat this like a function
// e.g. postOutput: OutputDispatcher<String> → postOutput("hello!")
```

> [!NOTE]
> Do not worry about the `Transferables` parameter for now. Transferable objects are a somewhat niche,
> performance-related feature, and they will be discussed later. It is not expected that a majority of workers will
> require them.

### I/O serialization

Finally, `IOSerializer` is responsible for marshalling objects between the worker and the application.

```kotlin
interface IOSerializer<I, O> {
  fun serializeInput(input: I): String
  fun deserializeInput(input: String): I
  fun serializeOutput(output: O): String
  fun deserializeOutput(output: String): O
}
```

This class allows you to use the serialization library of your choice. However, as you'll see later, this can be a
one-liner for developers using Kotlinx Serialization.

### Generated `Worker`

Once the Kobweb Worker Gradle plugin finds your worker factory implementation, it will generate a simple `Worker` class
that wraps it.

```kotlin
// Generated code!
class Worker(val onOutput: WorkerContext.(O) -> Unit) {
  fun postInput(input: I, transferables: Transferables = Transferables.Empty)
  fun terminate()
}
```

Applications will interact with this `Worker` and not the `WorkerStrategy` directly. In fact, you should make your
worker factory implementation `internal` to prevent applications from seeing anything but the worker.

You should think of the `WorkerStrategy` as representing implementation details while the `Worker` class represents a
public API. In other words, the `WorkerStrategy` receives inputs, processes data, and posts outputs, while the `Worker`
allows users to post inputs and get notified when outputs are sent back.

An application module (i.e. one that applies the Kobweb Application Gradle plugin) will automatically discover any
Kobweb worker dependencies, extracting their worker scripts and putting them under the `public/` folder of your final
site. This way, you don't have to do anything except depend on a worker module to use it.

## WorkerFactory examples

The following sections introduce concrete worker factories, which should help solidify the abstract concepts introduced
above.

### EchoWorkerFactory

The simplest worker strategy possible is one that blindly repeats back whatever text input it receives.

This is never a worker strategy that you'd actually create -- there wouldn't be a need for it -- but it's a good
starting point for seeing a worker factory in action.

When you have a worker strategy that works with raw strings like this one does, you can use a one-line helper method to
implement the `createIOSerializer` method, called `createPassThroughSerializer` (since it just passes the raw strings
through the serializer unmodified).

```kotlin
// Worker module
internal class EchoWorkerFactory : WorkerFactory<String, String> {
  override fun createStrategy(postOutput: OutputDispatcher<String>) = WorkerStrategy<String> { input ->
    postOutput(input)
  }
  override fun createIOSerializer() = createPassThroughSerializer()
}
```

> [!NOTE]
> The `WorkerStrategy<String> { ... }` call above is a convenience method provided by Kobweb that simplifies the
> boilerplate of implementing the `WorkerStrategy` class. It is nearly equivalent to:
> ```kotlin
> object : WorkerStrategy<String> {
>   override fun onInput(inputMessage: InputMessage<I>) { /* ... */ }
> }
> ```

Based on that implementation, a worker called `EchoWorker` will be auto-generated at compile time. Using it in your
application looks like this:

```kotlin
// Application module
val worker = rememberWorker {
  EchoWorker { message -> println("Echoed: $message") }
}

// Later
worker.postInput("hello!") // After a round trip: "Echoed: hello!"
```

That's it!

> [!IMPORTANT]
> Note the use of the `rememberWorker` method. This internally calls a `remember` but also sets up disposal logic that
> terminates the worker when the composable is exited. If you just use a normal `remember` block, the worker may keep
> running longer than you expect, even if you navigate to another part of your site.
>
> You can also stop a worker yourself by calling `worker.terminate()` directly.

### CountDownWorkerFactory

This next worker strategy will take in an `Int` value from the user. This number represents how many seconds to count
down, firing a message for each second that passes.

This is another strategy that you'd never need in practice -- you'd just use the `window.setInterval` method yourself
in your site script -- but we'll show this anyway to demonstrate two additional concepts on top of the echo worker:

* How to define a custom message serializer.
* The fact that you can call `postOutput` as often as you want.

```kotlin
// Worker module
internal class CountDownWorkerFactory : WorkerFactory<Int, Int> {
  override fun createStrategy(postOutput: OutputDispatcher<Int>) = WorkerStrategy<Int> { input ->
    var nextCount = input
    var intervalId: Int = 0
    intervalId = self.setInterval({ // A
      postOutput(nextCount) // B
      if (nextCount > 0) {
        --nextCount
      } else {
        self.clearInterval(intervalId)
      }
    }, 1000)
  }

  // C
  override fun createIOSerializer() = object : IOSerializer<Int, Int> {
    override fun serializeInput(input: Int) = input.toString()
    override fun deserializeInput(input: String) = input.toInt()
    override fun serializeOutput(output: Int) = output.toString()
    override fun deserializeOutput(output: String) = output.toInt()
  }
}
```

Notice the three comment tags above.

* **A:** We use `self.setInterval` (and `self.clearInterval` later) instead of the `window` object to do this. This is
  because the `window` object is only available in the main script and referencing it here will throw an exception.
* **B:** You can use `postOutput` any time following an input message, not just in direct response to one.
* **C:** This is how you define a custom message serializer. You shouldn't worry about receiving improperly formatted
  strings in your `deserialize` calls, because you control them! In other words, the only way you'd get a bad string is
  if you generated it yourself in either of the `serialize` methods. If a message serializer ever does throw an
  exception, then the Kobweb worker will simply ignore it as a bad message.

Using the worker in your application looks like this:

```kotlin
// Application module
val worker = rememberWorker {
  CountDownWorker {
    if (it > 0) {
      console.log(it + "...")
    } else {
      console.log("HAPPY NEW YEAR!!!")
    }
  }
}

// Later
worker.postInput(10) // 10... 9... 8... etc.
```

> [!TIP]
> If you need really accurate, consistent interval timers, creating a worker like this may actually be beneficial.
> According to [this article](https://hackwild.com/article/web-worker-timers/), web worker timers are slightly more
> accurate than timers run in the main thread, as they don't have to compete with the rest of the site's
> responsibilities. Also, it seems that web workers timers stay consistent even if the site tab loses focus.

### FindPrimesWorkerFactory

Finally, we get to the worker idea we introduced in the very first section -- finding the first *N* primes.

This kind of worker looks like one that would actually get used in a real codebase -- that being a worker which
performs a potentially expensive, UI-agnostic calculation.

We'll also use this example to demonstrate how to use Kotlinx Serialization to easily declare rich input and output
message types.

First, add `kotlinx-serialization` and `kobwebx-serialization-kotlinx` to your dependencies:

```kotlin
// build.gradle.kts
kotlin {
  configAsKobwebWorker()
  jsMain.dependencies {
    implementation(libs.kotlinx.serialization.json) // or "org.jetbrains.kotlinx:kotlinx-serialization-json"
    implementation(libs.kobwebx.worker.kotlinx.serialization) // or "com.varabyte.kobwebx:kobwebx-serialization-kotlinx"
  }
}
```

Then, define the worker factory:

```kotlin
@Serializable
data class FindPrimesInput(val max: Int)

@Serializable
data class FindPrimesOutput(val max: Int, val primes: List<Int>)

private fun findPrimes(max: Int): List<Int> {
  // Loop through all numbers, taking out multiples of each prime
  // e.g. 2 will take out 4, 6, 8, 10, etc.
  // then 3 will take out 9, 15, 21, etc. (6, 12, and 18 were already removed)
  val primes = (1..max).toMutableList()
  var primeIndex = 1 // Skip index 0, which is 1.
  while (primeIndex < primes.lastIndex) {
    val prime = primes[primeIndex]
    var maybePrimeIndex = primeIndex + 1
    while (maybePrimeIndex <= primes.lastIndex) {
      if (primes[maybePrimeIndex] % prime == 0) {
        primes.removeAt(maybePrimeIndex)
      } else {
        ++maybePrimeIndex
      }
    }
    primeIndex++
  }
  return primes
}

internal class FindPrimesWorkerFactory: WorkerFactory<FindPrimesInput, FindPrimesOutput> {
  override fun createStrategy(postOutput: OutputDispatcher<FindPrimesOutput>) =
    object : WorkerStrategy<FindPrimesInput>() {
      override fun onInput(inputMessage: InputMessage<FindPrimesInput>) {
        val input = inputMessage.input
        postOutput(FindPrimesOutput(input.max, findPrimes(input.max)))
      }
    }

  override fun createIOSerializer() = Json.createIOSerializer<FindPrimesInput, FindPrimesOutput>()
}
```

Most of the complexity above is the `findPrimes` algorithm itself!

The `onInput` handler is about as easy as it gets. Notice that we pass the input `max` value back into the output, so
that the receiving application can easily correlate the output with the input.

And finally, note the use of the `Json.createIOSerializer` method call. This utility method comes from the
`kobwebx-serialization-kotlinx` dependency, allowing you to use a one-liner to implement all the serialization methods
for you.

> [!TIP]
> It's fairly trivial to write the message serializer yourself if you don't want to pull in the extra dependency (or if
> you are using a different serialization library):
>
> ```kotlin
> object : IOSerializer<FindPrimesInput, FindPrimesOutput> {
>   override fun serializeInput(input: FindPrimesInput): String = Json.encodeToString(input)
>   override fun deserializeInput(input: String): FindPrimesInput = Json.decodeFromString(input)
>   override fun serializeOutput(output: FindPrimesOutput): String = Json.encodeToString(output)
>   override fun deserializeOutput(output: String): FindPrimesOutput = Json.decodeFromString(output)
> }
> ```

Using the worker in your application looks like this:

```kotlin
// Application module
val worker = rememberWorker {
  FindPrimesWorker {
    println("Primes for ${it.max}: ${it.primes}")
  }
}

// Later
worker.postInput(FindPrimesInput(1000)) // Primes for 1000: [1, 2, 3, 5, 7, 11, ..., 977, 983, 991, 997]
```

The richly-typed input and output messages allow for a very explicit API here, and in the future, more parameters could
be added (with default values) to either input or output classes, extending the functionality of your workers without
breaking existing code.

We don't show it here, but you could also create sealed classes for your input and output messages, allowing you to
define multiple types of messages that your worker can receive and respond to.

## Transferables

Occasionally, you may find yourself with a very large blob of data in your main application that you want to pass to a
worker (or vice versa!). For example, maybe your worker will be responsible for processing a potentially large,
multi-megabyte image.

Serializing a large amount of data can be expensive! In fact, you may find that even though your worker can run
efficiently on a background thread, sending a large amount of data to it can cause your site to experience a significant
pause during the copy. This can easily be seconds if the data is large enough!

This isn't just an issue with Kobweb. This was originally a problem with standard web APIs. To support this use-case,
web workers introduced the concept
of [transferable objects](https://developer.mozilla.org/en-US/docs/Web/API/Web_Workers_API/Transferable_objects).

Instead of an object being copied over, its ownership is transferred over from one thread to another. Attempts to use
the object in the original thread after that point will throw an exception.

Kobweb workers support transferable objects in a type-safe, Kotlin-idiomatic way, via the `Transferables` class. Using
it, you can register named objects in one thread and then retrieve them by that name in another.

Here's an example where we send a very large array over to a worker.

```kotlin
// In your site:
val largeArray = Uint8Array(1024 * 1024 * 8).apply { /* initialize it */ }

worker.postInput(WorkerInput(), Transferables {
  add("largeArray", largeArray)
})

// In the worker:
val largeArray = transferables.getUint8Array("largeArray")!!
```

And, of course, workers can send transferable objects back to the main application as well.

```kotlin
// In the worker:
val largeArray = Uint8Array(1024 * 1024 * 8).apply { /* initialize it */ }
postOutput(WorkerOutput(), Transferables {
  add("largeArray", largeArray)
})

// In your site:
val worker = rememberWorker {
  ExampleWorker {
    val largeArray = transferables.getUint8Array("largeArray")!!
    // ...
  }
}
```

Finally, it's worth noting that not every object can be transferred. In fact, very few can! You can
refer to the official docs for
a [full list of supported transferable objects](https://developer.mozilla.org/en-US/docs/Web/API/Web_Workers_API/Transferable_objects#supported_objects).
When building a `Transferables` object, the `add` method is type-safe, meaning you cannot add an object that cannot then
be transferred over.

> [!CAUTION]
> Kotlin/JS does not support a majority of the classes listed at the link above, so neither does Kobweb as a result. If
> you find yourself needing one of these missing classes, consider
> [filing an issue](https://github.com/varabyte/kobweb/issues/new?assignees=&labels=enhancement&projects=&template=feature_request.md&title=).
> We might wrap the JavaScript class into Kobweb directly and update the Transferables API.

Despite official limitations, Kobweb actually offers support for a few additional types, as a convenience.

Typed arrays, such as `Int8Array`, are a great example. They are actually not transferable! Only their internal
`ArrayBuffer` is.

If it is possible to extract transferable content from an object, transfer *that*, and then build the original object
back up on the other end, we are happy to do that for you. When you ask Kobweb to transfer a typed array, it will
instead transfer its contents for you and regenerate the outer array seamlessly on the other end. This is just
boilerplate code that you would have had to write yourself anyway.

> [!TIP]
> Run `kobweb create examples/imageprocessor` to see a project which demonstrates workers leveraging `Transferables` to
> pass image data from the main thread to a worker and back.

## Final notes about worker factories

### One worker factory per module

Due to the fundamental design of web workers, you can only define a single worker per module. If you need multiple
workers, you must create multiple modules, each providing their own separate worker strategy.

The Kobweb Worker Gradle plugin will not compile if it finds more than one worker factory implemented in a module.

### Name constraint

By default, the Kobweb Worker Gradle plugin requires your worker factory class to be suffixed with `WorkerFactory` so it
has guidance on how to name the final worker (for example, `MyExampleWorkerFactory` would generate a worker called
`MyExampleWorker`, placing it in the same package as the factory class).

```kotlin
// ❌ The Kobweb Worker Gradle plugin will complain about this name!
internal class MyWorkerProvider : WorkerFactory<I, O> { /* ... */ }

// ✅ This is correct.
internal class MyWorkerWorkerFactory : WorkerFactory<I, O> { /* ... */ }
```

If you don't like this constraint, you can override the `kobweb.worker.fqcn` property in your build script to provide
a worker name explicitly:

```kotlin
// build.gradle.kts
kobweb {
  worker {
    fqcn.set("com.mysite.MyWorker")
  }
}
```

at which point, you are free to name your worker factory whatever you like.

If you want to just change the name of your worker, using the same package as the worker factory, you can omit the
package:

```kotlin
// build.gradle.kts
kobweb {
  worker {
    fqcn.set(".MyWorker")
  }
}
```

## When to use Kobweb Workers

In practice, almost every site can get away without ever using a worker, especially in Kotlin/JS where you can
leverage coroutines as a way to mimic concurrency in your single-threaded site.

That said, if you know your site is going to run some logic that is not concerned with the DOM at all, and especially
which might take a long time to run, separating that out into its own worker can be a sensible approach.

By isolating your logic into a separate worker, you not only keep it from potentially freezing your UI, but you also
guarantee that it will be strongly decoupled from the rest of your site, preventing future developers from introducing
potential spaghetti code issues in the future.

Another interesting use-case for a worker is isolating some sort of complex state management, where encapsulating that
complexity keeps the rest of your site easier to reason about.

For example, maybe you're making a web game, and you decide to create a worker to manage all the game logic. You could
of course create a Kobweb library for the same effect, but using a worker has a stronger guarantee that the logic will
never interact directly with your site's UI.

> [!CAUTION]
> You should be aware that, since a web worker is a whole separate standalone script, it needs to include its own copy
> of the Kotlin/JS runtime, even though your main site already has its own copy.
>
> Even after running a dead-code elimination pass, I found that the trivial echo worker's final output was about 200K.
> However, this did compress down to 60K before being sent over the wire.
>
> For most practical use-cases, a 60K download is not a deal-breaker, especially as most images are many multiples
> larger than that. But developers should be aware of this, and if this is indeed a concern, you may decide to avoid
> using Kobweb workers on your site.
