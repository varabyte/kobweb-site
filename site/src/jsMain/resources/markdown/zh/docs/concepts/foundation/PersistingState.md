---
description: 如何使用 Web 存储来持久化跨页面甚至跨浏览会话的状态。
title: 状态持久化
follows: ApplicationGlobals
---

在一个页面上设置一个值并希望在其他页面上可用是很常见的需求，比如购物车内容。或者你可能希望当用户将来再次访问页面时能够恢复某个值，即使他们已经关闭并重新打开了浏览器，比如用户偏好设置。

## Web 存储

在 Web 开发中，这是通过 [web storage](https://developer.mozilla.org/en-US/docs/Web/API/Web_Storage_API) 来实现的，它有两种类型：
*本地存储（local storage）*和*会话存储（session storage）*。

本地存储和会话存储具有相同的 API，主要区别在于它们的生命周期。本地存储的值会一直保存，直到用户清除浏览器缓存，而会话存储的值会在用户关闭当前标签页时消失。

正如你所料，本地存储适用于需要无限期保存的值。用户偏好设置就是一个常见的用例。例如，许多 Kobweb 网站会在本地存储中保存用户最后选择的颜色模式。

同时，会话存储适用于只需要在用户与网站交互期间保存的数据。例如，你可能会跟踪尚未提交到服务器的文本字段中输入的值，以防用户不小心重新加载页面（页面重新加载不会结束会话）。

在 Kotlin 中使用存储 API 非常简单 -- 只需引用 `kotlinx.browser.localStorage` 和 `kotlinx.browser.sessionStorage` 对象，它们都是 `Storage` 类型：

```kotlin
// Note: Several fields elided for simplicity...
interface Storage {
    fun getItem(key: String): String?
    fun setItem(key: String, value: String)
}
```

```kotlin
import kotlinx.browser.localStorage

localStorage.setItem("example-key", "example-value")
assert(localStorage.getItem("example-key") == "example-value")
```

通过这些 API，开发者可以在访问页面时检查存储中是否存在预期的值并相应地采取行动，例如，如果检测到用户未登录，则将用户重定向到登录页面。

## 类型安全的存储值

基本的存储 API 允许你设置和检索字符串值。在此基础上，Kobweb 添加了各种 `StorageKey` 实用工具类，以支持创建和查询类型安全的存储值。

例如，如果你想存储一个整数值，可以这样做：

```kotlin
const val DEFAULT_BRIGHTNESS = 100
val BRIGHTNESS_KEY = IntStorageKey("brightness")

localStorage.setItem(BRIGHTNESS_KEY, DEFAULT_BRIGHTNESS)
val brightness = localStorage.getItem(BRIGHTNESS_KEY) ?: DEFAULT_BRIGHTNESS
```

> [!TIP]
> 所有 `StorageKey` 构造函数都可以接受一个可选的 `defaultValue` 参数，这可以帮助减少上述代码中的样板代码：
>
> ```kotlin
> val BRIGHTNESS_KEY = IntStorageKey("brightness", defaultValue = 100)
>
> val brightness = localStorage.getItem(BRIGHTNESS_KEY)!!
> ```

你也可以通过提供自己的 *toString* 和 *fromString* 转换函数来创建自定义实现：

```kotlin
class User(val name: String, val id: String)

class UserStorageKey(name: String) : StorageKey<User>(name) {
    override fun convertToString(value: User) = "$name:$id"
    override fun convertFromString(value: String): User? = value.split(":")
        .takeIf { it.size == 2}
        ?.let { User(it[0], it[1]) }
}

val LOGGED_IN_USER_KEY = UserStorageKey("logged-in-user")

val loggedInUser = localStorage.getItem(LOGGED_IN_USER_KEY)
```

> [!TIP]
> 如果你在项目中使用了 Kotlinx 序列化，你可以使用 `@Serializable` 和 `Json.decodeFromString` / `Json.encodeToString` 来简化上述代码：
>
> ```kotlin
> @Serializable
> class User(val name: String, val id: String)
> 
> class UserStorageKey(name: String) : StorageKey<User>(name) {
>     override fun convertToString(value: User): String = Json.encodeToString(value)
>     override fun convertFromString(value: String): User? =
>         try { Json.decodeFromString(value) } catch (ex: Exception) { null }
> }
> ```
