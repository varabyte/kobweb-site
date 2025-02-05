---
follows: ServerLogs
---

Many users who create a full stack application generally expect to completely own both the client- and server-side code.

However, being an opinionated framework, Kobweb provides a custom Ktor server in order to deliver some of its features.
For example, it implements the logic for handling [server API routes▲](#define-api-routes) as well as some live reloading
functionality.

It would not be trivial to refactor this behavior into some library that users could import into their own backend
server. As a compromise, some server configuration is exposed by the `.kobweb/conf.yaml` file, and this has been the
main way users could affect the server's behavior.

That said, there will always be some use cases that Kobweb won't anticipate. So as an escape hatch, Kobweb allows users
who know what they're doing to write their own plugins to extend the server.

> [!NOTE]
> The Kobweb Server plugin feature is still fairly new. If you use it, please consider
> [filing issues](https://github.com/varabyte/kobweb/issues/new?assignees=&labels=enhancement&projects=&template=feature_request.md&title=)
> for any missing features and [connecting with us▼](#connecting-with-us) to share any feedback you have about your
> experience.

Creating a Kobweb server plugin is relatively straightforward. You'll need to:

* Create a new module in your project that produces a JAR file that bundles an implementation of
  the `KobwebServerPlugin` interface.
* Add that module as a `kobwebServerPlugin` dependency in your site's build script.
    * This ensures a copy of that jar is put under your project's `.kobweb/server/plugins` directory.

### Create a Kobweb Server Plugin

The following steps will walk you through creating your first Kobweb Server Plugin.

> [!TIP]
> You can
> download [this project](https://github.com/varabyte/data/raw/main/kobweb/projects/serverplugin.zip) to see the
> completed result from applying the instructions in this section to the `kobweb create app` site.

* Create a new module in your project.
    * For example, name it "demo-server-plugin".
    * Be sure to update your `settings.gradle.kts` file to include the new project.
* Add new entries for the `kobweb-server-project` library and kotlin JVM plugin in `.gradle/libs.versions.toml`:
  ```toml
  [libraries]
  kobweb-server-plugin = { module = "com.varabyte.kobweb:kobweb-server-plugin", version.ref = "kobweb" }

  [plugins]
  kotlin-jvm = { id = "org.jetbrains.kotlin.jvm", version.ref = "kotlin" }
  ```
* **For all remaining steps, create all files / directories under your new module's directory (e.g. `demo-server-plugin/`).**
* Create `build.gradle.kts`:
  ```kotlin
    plugins {
      alias(libs.plugins.kotlin.jvm)
    }
    group = "org.example.app" // update to your own project's group
    version = "1.0-SNAPSHOT"

    dependencies {
      compileOnly(libs.kobweb.server.plugin)
    }
  ```
* Create `src/main/kotlin/DemoKobwebServerPlugin.kt`:
  ```kotlin
  import com.varabyte.kobweb.server.plugin.KobwebServerPlugin
  import io.ktor.server.application.Application
  import io.ktor.server.application.log

  class DemoKobwebServerPlugin : KobwebServerPlugin {
    override fun configure(application: Application) {
      application.log.info("REPLACE ME WITH REAL CONFIGURATION")
    }
  }
  ```
> [!TIP]
> As the Kobweb server is written in Ktor, you should familiarize yourself with [Ktor's documentation](https://ktor.io/docs/plugins.html).
* Create `src/main/resources/META-INF/services/com.varabyte.kobweb.server.plugin.KobwebServerPlugin`, setting its
  content to the fully-qualified class name of your plugin. For example:
  ```text
  org.example.app.DemoKobwebServerPlugin
  ```
> [!NOTE]
> If you aren't familiar with `META-INF/services`, you can
> read [this helpful article](https://www.baeldung.com/java-spi) to learn more about service implementations, a very
> useful Java feature.

### Register your server plugin jar

The Kobweb Gradle Application plugin provides a way to notify it about your JAR project. Set it up, and Gradle will
build and copy your plugin jar over for you automatically.

In your Kobweb project's build script, include the following `kobwebServerPlugin` line in a top-level dependencies
block:

```kotlin
// site/build.gradle.kts

dependencies {
  kobwebServerPlugin(project(":demo-server-plugin"))
}

kotlin { /* ... */ }
```

> [!IMPORTANT]
> You need to put the `kobwebServerPlugin` declaration inside a top-level `dependencies` block, not in one of the
> ones nested under the `kotlin` block (such as `kotlin.jvmMain.dependencies`).

Once this is set up, upon the next Kobweb server run (e.g. via `kobweb run`), if you check the logs, you should see
something like this:

```text
[main] INFO  ktor.application - Autoreload is disabled because the development mode is off.
[main] INFO  ktor.application - REPLACE ME WITH REAL CONFIGURATION
[main] INFO  ktor.application - Application started in 0.112 seconds.
[main] INFO  ktor.application - Responding at http://0.0.0.0:8080
```

### Hooking into Ktor routing events

Despite the simplicity of the `KobwebServerPlugin` interface, the `application` parameter passed
into `KobwebServerPlugin.configure` is quite powerful.

While I know it may sound kind of meta, you can create and install a Ktor Application Plugin inside a Kobweb Server
Plugin. Once you've done that, you have access to all stages of a network call, as well as some other hooks like ones
for receiving Application lifecycle events.

> [!TIP]
> Please read the [Extending Ktor documentation](https://ktor.io/docs/custom-plugins.html) to learn more.

Doing so looks like this:

```kotlin
import com.varabyte.kobweb.server.plugin.KobwebServerPlugin
import io.ktor.server.application.Application
import io.ktor.server.application.createApplicationPlugin
import io.ktor.server.application.install

class DemoKobwebServerPlugin : KobwebServerPlugin {
  override fun configure(application: Application) {
    val demo = createApplicationPlugin("DemoKobwebServerPlugin") {
      onCall { call -> /* ... */ } // Request comes in
      onCallRespond { call -> /* ... */ } // Response goes out
    }
    application.install(demo)
  }
}
```

### Changing a Kobweb Server Plugin requires a server restart

It's important to note that, unlike other parts of Kobweb, Kobweb Server Plugins do NOT support live reloading. We only
start up and configure a Kobweb server once in its lifetime.

If you make a change to a Kobweb Server Plugin, you must quit and restart the server for it to take effect.
