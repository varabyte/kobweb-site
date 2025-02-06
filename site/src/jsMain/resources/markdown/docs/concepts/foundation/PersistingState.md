---
follows: ApplicationGlobals
---

It is very common to want to set a value on one page that should be made available on other pages, such as shopping cart
contents. Or maybe you want a value to be restored when a user returns to the page again in the future, even if they've
since closed and reopened the browser, such as a user preference.

## Web storage

In the world of web development, this is accomplished
with [web storage](https://developer.mozilla.org/en-US/docs/Web/API/Web_Storage_API), of which there are two flavors:
*local storage* and *session storage*.

Local storage and web storage have identical APIs, with the key difference being their lifetimes. Local storage values
will last until the user clears their browser's cache, while session storage will last until the user closes the current
tab.

As you might expect, local storage is useful for values that should stick around indefinitely. User preferences are
a common use case here. Many Kobweb sites save the user's last selected color mode in local storage, for example.

Meanwhile, session storage is useful when you want to persist data just as long as the user is interacting with your
site but no longer. For example, you might keep track of values typed into text fields that haven't been submitted to
the server yet, just in case the user reloads the page by accident (page reloads do not end sessions).

Using the storage APIs in Kotlin is trivial -- just reference the `kotlinx.browser.localStorage` and
`kotlinx.browser.sessionStorage` objects, which are both of type `Storage`:

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

With these APIs, the developer can check if an expected value is present in storage or not when visiting a page and act
accordingly, for example by re-routing users to a login page if they detect the user is not logged in.

## Type-safe storage values

The basic storage APIs let you set and retrieve string values. On top of this, Kobweb adds various `StorageKey` utility
classes to enable the creation and querying of type-safe storage values.

For example, if you want to store an integer value, you can do so like this:

```kotlin
const val DEFAULT_BRIGHTNESS = 100
val BRIGHTNESS_KEY = IntStorageKey("brightness")

localStorage.setItem(BRIGHTNESS_KEY, DEFAULT_BRIGHTNESS)
val brightness = localStorage.getItem(BRIGHTNESS_KEY) ?: DEFAULT_BRIGHTNESS
```

> [!TIP]
> All `StorageKey` constructors can take an optional `defaultValue` parameter, which can help reduce some of the
boilerplate in the above code:
>
> ```kotlin
> val BRIGHTNESS_KEY = IntStorageKey("brightness", defaultValue = 100)
>
> val brightness = localStorage.getItem(BRIGHTNESS_KEY)!!
> ```

You can also create your own custom implementations by providing your own *toString* and *fromString* conversion
functions:

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
> If you are using Kotlinx serialization in your project, you can use it to simplify the above code using
> `@Serializable` and `Json.decodeFromString` / `Json.encodeToString`:
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
