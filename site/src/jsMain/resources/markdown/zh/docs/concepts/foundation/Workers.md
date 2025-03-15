---
follows: Markdown
---

[Web workers](https://developer.mozilla.org/en-US/docs/Web/API/Web_Workers_API) 是一种标准的 Web 技术，允许你在主应用程序之外的单独线程中运行 JavaScript 代码。虽然 JavaScript 以单线程著称，但 web workers 提供了一种方式，让你可以并行运行潜在的耗时代码，而不会减慢主站点的速度。

Web worker 脚本完全独立于主站点，无法访问 DOM。它们之间唯一的通信方式是通过消息传递。

> [!NOTE]
> 细心的读者可能会认出这里的 [actor 模型](https://en.wikipedia.org/wiki/Actor_model)，这是一种有效的方式，可以实现并发而不用担心常见的基于锁的方法带来的同步问题。

一个有点牵强但容易理解的 web worker 示例是计算前 N 个质数。

当 worker 在进行密集计算时，你的网站仍然可以正常运行，保持完全响应。当 worker 完成时，它会向应用程序发送一个消息，应用程序通过更新相关的 UI 元素来处理它。

## Kobweb 中的 Web workers

Kobweb 旨在让使用 web workers 变得尽可能简单，同时在其之上添加类型安全层。

以下是你需要做的所有事情（我们将在下面展示这些步骤的具体示例）：

* 创建一个新模块并应用 Kobweb Worker Gradle 插件。
* 在构建脚本的 `kotlin { ... }` 块中使用 `configAsKobwebWorker()` 调用进行标记。
    * （可选但推荐）为你的 worker 指定一个名称。否则，将使用通用名称 "worker"（带有短随机后缀），这虽然可以工作但在出现问题时可能更难调试。
* 声明对 `"com.varabyte.kobweb:kobweb-worker"` 的依赖。
* 实现 `WorkerFactory` 接口，提供代表 worker 核心逻辑的 `WorkerStrategy`。

### 构建脚本

```kotlin
import com.varabyte.kobweb.gradle.worker.util.configAsKobwebWorker

plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.kobweb.worker) // 或 id("com.varabyte.kobweb.worker")
}

group = "example.worker"
version = "1.0-SNAPSHOT"

kotlin {
    configAsKobwebWorker("example-worker")
    sourceSets {
        jsMain.dependencies {
            implementation(libs.kobweb.worker) // 或 "com.varabyte.kobweb:kobweb-worker"
        }
    }
}
```

### Worker 工厂

`WorkerFactory` 接口非常简单：

```kotlin
interface WorkerFactory<I, O> {
  fun createStrategy(postOutput: OutputDispatcher<O>): WorkerStrategy<I>
  fun createIOSerializer(): IOSerializer<I, O>
}
```

这个简洁的接口包含了很多信息。你的实现将指定：

* Worker 接受什么类型的输入和输出消息。
* 如何序列化这些输入和输出消息。
* 处理来自应用程序的输入消息的逻辑（通过你的 `WorkerStrategy` 实现）。
* 向应用程序发送消息的逻辑（通过 `postOutput` 对象）。

### Worker 策略

`WorkerStrategy` 类允许你定义 worker 如何处理从应用程序收到的输入。它还通过 
[`DedicatedWorkerGlobalScope`](https://developer.mozilla.org/en-US/docs/Web/API/DedicatedWorkerGlobalScope) 对象暴露了一个 `self` 属性，提供标准库 worker 功能。

```kotlin
abstract class WorkerStrategy<I> {
  protected val self: DedicatedWorkerGlobalScope
  abstract fun onInput(inputMessage: InputMessage<I>)
}
```

### 输出调度器

`OutputDispatcher` 是一个简单的类，允许你向应用程序发送输出消息。

```kotlin
class OutputDispatcher<O> {
  operator fun invoke(output: O, transferables: Transferables = Transferables.Empty)
}

// 注意这里的 invoke 操作符，所以你可以像函数一样使用它
// 例如 postOutput: OutputDispatcher<String> → postOutput("hello!")
```

> [!NOTE]
> 暂时不用担心 `Transferables` 参数。可传输对象是一个有点小众的、与性能相关的功能，我们稍后会讨论。大多数 workers 预计不会需要它们。

### I/O 序列化

最后，`IOSerializer` 负责在 worker 和应用程序之间序列化对象。

```kotlin
interface IOSerializer<I, O> {
  fun serializeInput(input: I): String
  fun deserializeInput(input: String): I
  fun serializeOutput(output: O): String
  fun deserializeOutput(output: String): O
}
```

这个类允许你使用你选择的序列化库。不过，正如你稍后会看到的，对于使用 Kotlinx Serialization 的开发者来说，这可能只需要一行代码。

### 生成的 `Worker`

一旦 Kobweb Worker Gradle 插件找到你的 worker 工厂实现，它将生成一个简单的 `Worker` 类来包装它。

```kotlin
// 生成的代码！
class Worker(val onOutput: WorkerContext.(O) -> Unit) {
  fun postInput(input: I, transferables: Transferables = Transferables.Empty)
  fun terminate()
}
```

应用程序将直接与这个 `Worker` 交互，而不是直接与 `WorkerStrategy` 交互。事实上，你应该将你的 worker 工厂实现标记为 `internal`，以防止应用程序看到除 worker 之外的任何内容。

你应该将 `WorkerStrategy` 视为实现细节，而 `Worker` 类代表公共 API。换句话说，`WorkerStrategy` 接收输入、处理数据并发送输出，而 `Worker` 允许用户发送输入并在发送输出时得到通知。

应用程序模块（即应用 Kobweb Application Gradle 插件的模块）将自动发现任何 Kobweb worker 依赖，提取它们的 worker 脚本并将它们放在最终站点的 `public/` 文件夹下。这样，你只需要依赖 worker 模块就可以使用它。

## WorkerFactory 示例

以下部分介绍具体的 worker 工厂，这将有助于巩固上面介绍的抽象概念。

### EchoWorkerFactory

最简单的 worker 策略是盲目重复它收到的任何文本输入。

这永远不会是你真正需要创建的 worker 策略 —— 没有这个必要 —— 但它是一个很好的起点，可以看到 worker 工厂的实际运作。

当你有一个像这样处理原始字符串的 worker 策略时，你可以使用一个单行帮助方法来实现 `createIOSerializer` 方法，称为 `createPassThroughSerializer`（因为它只是将原始字符串不加修改地通过序列化器传递）。

```kotlin
// Worker 模块
internal class EchoWorkerFactory : WorkerFactory<String, String> {
  override fun createStrategy(postOutput: OutputDispatcher<String>) = WorkerStrategy<String> { input ->
    postOutput(input)
  }
  override fun createIOSerializer() = createPassThroughSerializer()
}
```

> [!NOTE]
> 上面的 `WorkerStrategy<String> { ... }` 调用是 Kobweb 提供的便利方法，简化了实现 `WorkerStrategy` 类的样板代码。它几乎等同于：
> ```kotlin
> object : WorkerStrategy<String> {
>   override fun onInput(inputMessage: InputMessage<I>) { /* ... */ }
> }
> ```

基于该实现，一个名为 `EchoWorker` 的 worker 将在编译时自动生成。在你的应用程序中使用它看起来像这样：

```kotlin
// 应用程序模块
val worker = rememberWorker {
  EchoWorker { message -> println("Echoed: $message") }
}

// 之后
worker.postInput("hello!") // 经过一个来回后：" Echoed: hello!"
```

就是这样！

> [!IMPORTANT]
> 注意 `rememberWorker` 方法的使用。这在内部调用了 `remember`，但同时也设置了处理逻辑，当可组合退出时终止 worker。如果你只是使用普通的 `remember` 块，worker 可能会运行得比你预期的时间更长，即使你导航到站点的其他部分。
>
> 你也可以通过直接调用 `worker.terminate()` 来停止 worker。

### CountDownWorkerFactory

这个下一个 worker 策略将从用户那里接收一个 `Int` 值。这个数字表示要倒数的秒数，每过一秒就发送一个消息。

这是另一个你在实践中永远不会需要的策略 —— 你可以直接在站点脚本中使用 `window.setInterval` 方法 —— 但我们还是会展示这个例子，以演示在回声 worker 之上的两个额外概念：

* 如何定义自定义消息序列化器。
* 你可以根据需要多次调用 `postOutput`。

```kotlin
// Worker 模块
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

注意上面的三个注释标记。

* **A:** 我们使用 `self.setInterval`（稍后使用 `self.clearInterval`）而不是 `window` 对象来做这件事。这是因为 `window` 对象只在主脚本中可用，在这里引用它会抛出异常。
* **B:** 你可以在收到输入消息后的任何时候使用 `postOutput`，而不仅仅是直接响应输入时。
* **C:** 这就是如何定义自定义消息序列化器。你不应该担心在 `deserialize` 调用中收到格式不正确的字符串，因为你控制着它们！换句话说，只有当你在任一 `serialize` 方法中自己生成了一个错误的字符串时，才会得到一个错误的字符串。如果消息序列化器确实抛出异常，那么 Kobweb worker 将简单地忽略它作为一个错误消息。

在你的应用程序中使用 worker 看起来像这样：

```kotlin
// 应用程序模块
val worker = rememberWorker {
  CountDownWorker {
    if (it > 0) {
      console.log(it + "...")
    } else {
      console.log("新年快乐！！！")
    }
  }
}

// 之后
worker.postInput(10) // 10... 9... 8... 等等
```

> [!TIP]
> 如果你需要非常准确、一致的间隔计时器，创建这样的 worker 可能实际上是有益的。根据[这篇文章](https://hackwild.com/article/web-worker-timers/)，web worker 计时器比主线程中运行的计时器稍微准确一些，因为它们不必与站点的其他职责竞争。此外，即使站点标签失去焦点，web workers 计时器似乎也保持一致。

### FindPrimesWorkerFactory

最后，我们来到了我们在第一节中介绍的 worker 想法 —— 找到前 *N* 个质数。

这种 worker 看起来像是会在实际代码库中使用的那种 —— 也就是执行潜在昂贵的、与 UI 无关的计算的 worker。

我们还将使用这个例子来演示如何使用 Kotlinx Serialization 轻松声明丰富的输入和输出消息类型。

首先，将 `kotlinx-serialization` 和 `kobwebx-serialization-kotlinx` 添加到你的依赖中：

```kotlin
// build.gradle.kts
kotlin {
  configAsKobwebWorker()
  jsMain.dependencies {
    implementation(libs.kotlinx.serialization.json) // 或 "org.jetbrains.kotlinx:kotlinx-serialization-json"
    implementation(libs.kobwebx.worker.kotlinx.serialization) // 或 "com.varabyte.kobwebx:kobwebx-serialization-kotlinx"
  }
}
```

然后，定义 worker 工厂：

```kotlin
@Serializable
data class FindPrimesInput(val max: Int)

@Serializable
data class FindPrimesOutput(val max: Int, val primes: List<Int>)

private fun findPrimes(max: Int): List<Int> {
  // 遍历所有数字，去掉每个质数的倍数
  // 例如 2 会去掉 4、6、8、10 等
  // 然后 3 会去掉 9、15、21 等（6、12 和 18 已经被移除）
  val primes = (1..max).toMutableList()
  var primeIndex = 1 // 跳过索引 0，即 1。
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

上面大部分的复杂性都在 `findPrimes` 算法本身！

`onInput` 处理器非常简单。注意，我们将输入的 `max` 值传回输出，这样接收应用程序可以轻松地将输出与输入关联起来。

最后，注意 `Json.createIOSerializer` 方法调用的使用。这个实用方法来自 `kobwebx-serialization-kotlinx` 依赖，允许你使用一行代码来实现所有序列化方法。

> [!TIP]
> 如果你不想引入额外的依赖（或者如果你使用的是不同的序列化库），自己编写消息序列化器是相当简单的：
>
> ```kotlin
> object : IOSerializer<FindPrimesInput, FindPrimesOutput> {
>   override fun serializeInput(input: FindPrimesInput): String = Json.encodeToString(input)
>   override fun deserializeInput(input: String): FindPrimesInput = Json.decodeFromString(input)
>   override fun serializeOutput(output: FindPrimesOutput): String = Json.encodeToString(output)
>   override fun deserializeOutput(output: String): FindPrimesOutput = Json.decodeFromString(output)
> }
> ```

在你的应用程序中使用 worker 看起来像这样：

```kotlin
// 应用程序模块
val worker = rememberWorker {
  FindPrimesWorker {
    println("${it.max} 的质数：${it.primes}")
  }
}

// 之后
worker.postInput(FindPrimesInput(1000)) // 1000 的质数：[1, 2, 3, 5, 7, 11, ..., 977, 983, 991, 997]
```

丰富类型的输入和输出消息允许在这里提供一个非常明确的 API，并且将来可以向输入或输出类添加更多参数（带有默认值），扩展你的 workers 的功能而不破坏现有代码。

我们在这里没有展示，但你也可以为你的输入和输出消息创建密封类，允许你定义你的 worker 可以接收和响应的多种类型的消息。

## 可传输对象

有时，你可能会发现你的主应用程序中有一个非常大的数据块要传递给 worker（或反之！）。例如，也许你的 worker 将负责处理一个可能很大的、多兆字节的图像。

序列化大量数据可能很昂贵！事实上，你可能会发现，即使你的 worker 可以在后台线程上高效运行，但在复制过程中发送大量数据可能会导致你的站点出现显著的暂停。如果数据足够大，这很容易达到几秒钟！

这不仅仅是 Kobweb 的问题。这最初是标准 web API 的一个问题。为了支持这个用例，web workers 引入了[可传输对象](https://developer.mozilla.org/en-US/docs/Web/API/Web_Workers_API/Transferable_objects)的概念。

对象不是被复制过去，而是它的所有权从一个线程转移到另一个线程。在转移后尝试在原始线程中使用该对象将抛出异常。

Kobweb workers 通过 `Transferables` 类以类型安全、Kotlin 习惯的方式支持可传输对象。使用它，你可以在一个线程中注册命名对象，然后在另一个线程中通过该名称检索它们。

这里有一个例子，我们将一个非常大的数组发送给 worker。

```kotlin
// 在你的站点中：
val largeArray = Uint8Array(1024 * 1024 * 8).apply { /* 初始化它 */ }

worker.postInput(WorkerInput(), Transferables {
  add("largeArray", largeArray)
})

// 在 worker 中：
val largeArray = transferables.getUint8Array("largeArray")!!
```

当然，workers 也可以将可传输对象发送回主应用程序。

```kotlin
// 在 worker 中：
val largeArray = Uint8Array(1024 * 1024 * 8).apply { /* 初始化它 */ }
postOutput(WorkerOutput(), Transferables {
  add("largeArray", largeArray)
})

// 在你的站点中：
val worker = rememberWorker {
  ExampleWorker {
    val largeArray = transferables.getUint8Array("largeArray")!!
    // ...
  }
}
```

最后，值得注意的是，并不是每个对象都可以传输。事实上，很少有对象可以！你可以参考官方文档获取[支持的可传输对象的完整列表](https://developer.mozilla.org/en-US/docs/Web/API/Web_Workers_API/Transferable_objects#supported_objects)。
在构建 `Transferables` 对象时，`add` 方法是类型安全的，这意味着你不能添加一个之后无法传输的对象。

> [!CAUTION]
> Kotlin/JS 不支持上述链接中列出的大多数类，因此 Kobweb 也不支持。如果你发现你需要这些缺失的类中的一个，请考虑[提交问题](https://github.com/varabyte/kobweb/issues/new?assignees=&labels=enhancement&projects=&template=feature_request.md&title=)。
> 我们可能会直接将 JavaScript 类包装到 Kobweb 中并更新 Transferables API。

尽管有官方限制，但 Kobweb 实际上为了方便起见提供了对一些额外类型的支持。

类型化数组，如 `Int8Array`，就是一个很好的例子。它们实际上不是可传输的！只有它们的内部 `ArrayBuffer` 是。

如果可以从对象中提取可传输内容，传输*那个*，然后在另一端重建原始对象，我们很乐意为你做这件事。当你要求 Kobweb 传输一个类型化数组时，它会转而传输其内容，并在另一端无缝地重新生成外部数组。这只是你原本必须自己编写的样板代码。

> [!TIP]
> 运行 `kobweb create examples/imageprocessor` 来查看一个项目，它演示了 workers 利用 `Transferables` 在主线程和 worker 之间传递图像数据。

## 关于 worker 工厂的最后说明

### 每个模块一个 worker 工厂

由于 web workers 的基本设计，你每个模块只能定义一个 worker。如果你需要多个 workers，你必须创建多个模块，每个模块提供它们自己的单独 worker 策略。

如果 Kobweb Worker Gradle 插件在一个模块中发现多个 worker 工厂实现，它将不会编译。

### 名称约束

默认情况下，Kobweb Worker Gradle 插件要求你的 worker 工厂类以 `WorkerFactory` 为后缀，这样它就有指导如何命名最终的 worker（例如，`MyExampleWorkerFactory` 将生成一个名为 `MyExampleWorker` 的 worker，将它放在与工厂类相同的包中）。

```kotlin
// ❌ Kobweb Worker Gradle 插件将会抱怨这个名字！
internal class MyWorkerProvider : WorkerFactory<I, O> { /* ... */ }

// ✅ 这是正确的。
internal class MyWorkerWorkerFactory : WorkerFactory<I, O> { /* ... */ }
```

如果你不喜欢这个约束，你可以在你的构建脚本中覆盖 `kobweb.worker.fqcn` 属性来显式提供一个 worker 名称：

```kotlin
// build.gradle.kts
kobweb {
  worker {
    fqcn.set("com.mysite.MyWorker")
  }
}
```

这时，你可以随意命名你的 worker 工厂。

如果你只想更改 worker 的名称，使用与 worker 工厂相同的包，你可以省略包名：

```kotlin
// build.gradle.kts
kobweb {
  worker {
    fqcn.set(".MyWorker")
  }
}
```

## 何时使用 Kobweb Workers

在实践中，几乎每个站点都可以不使用 worker，特别是在 Kotlin/JS 中，你可以利用协程作为一种在你的单线程站点中模拟并发的方式。

话虽如此，如果你知道你的站点将运行一些完全不关心 DOM 的逻辑，特别是可能需要很长时间运行的逻辑，将其分离到自己的 worker 中可能是一种明智的做法。

通过将你的逻辑隔离到一个单独的 worker 中，你不仅可以防止它可能冻结你的 UI，而且还可以保证它与你站点的其余部分强烈解耦，防止未来的开发者在将来引入潜在的意大利面条代码问题。

worker 的另一个有趣的用例是隔离某种复杂的状态管理，其中封装该复杂性使你站点的其余部分更容易理解。

例如，也许你正在制作一个网页游戏，你决定创建一个 worker 来管理所有的游戏逻辑。你当然可以创建一个 Kobweb 库来达到相同的效果，但使用 worker 可以更强地保证逻辑永远不会直接与你站点的 UI 交互。

> [!CAUTION]
> 你应该意识到，由于 web worker 是一个完全独立的独立脚本，它需要包含自己的 Kotlin/JS 运行时副本，即使你的主站点已经有了自己的副本。
>
> 即使在运行死代码消除传递后，我发现琐碎的回声 worker 的最终输出约为 200K。
> 然而，在通过网络发送之前，这确实压缩到了 60K。
>
> 对于大多数实际用例来说，60K 的下载并不是一个破坏性的问题，特别是因为大多数图像的大小都是这个的很多倍。但开发者应该意识到这一点，如果这确实是一个问题，你可能会决定避免在你的站点上使用 Kobweb workers。
