---
follows: Index
---

A Kobweb project always has a frontend and, if configured as a full stack site, a backend as well. Both require
different steps to debug them.

## Debugging the frontend

At the moment, attaching a debugger to Kotlin/JS code requires IntelliJ Ultimate. If you have it, you
can [follow these steps](https://kotlinlang.org/docs/js-debugging.html#debug-in-the-ide) in the official docs.

> [!IMPORTANT]
> Be sure the port in your URL matches the port you specified in your `.kobweb/conf.yaml` file. By default, this
> is 8080.

If you do not have access to IntelliJ Ultimate, then you'll have to rely on `println` debugging. While this is far from
great, live reloading plus Kotlin's type system generally help you incrementally build your site up without too many
issues.

> [!TIP]
> If you're a student, you can apply for a free IntelliJ Ultimate
> license [here](https://www.jetbrains.com/community/education/#students). If you maintain an open source project, you
> can apply [here](https://www.jetbrains.com/community/opensource/#support).

## Debugging the backend

Debugging the backend first requires configuring the Kobweb server to
support [remote debugging](https://en.wikipedia.org/wiki/Debugging#Remote_debugging). This is easy to do by modifying
the `kobweb` block in your build script to enable it:

```kotlin
kobweb {
  app {
    server {
      remoteDebugging {
        enabled.set(true)
        port.set(5005)
      }
    }
  }
}
```

> [!NOTE]
> Specifying the port is optional. Otherwise, it is 5005, a common remote debugging default. If you ever need to debug
> multiple Kobweb servers at the same time, however, it can be useful to change it.

Once you've enabled remote debugging support, you can
then [follow the official documentation](https://www.jetbrains.com/help/idea/attaching-to-local-process.html#attach-to-remote)
to add a *remote JVM debug* configuration to your IDE.

> [!IMPORTANT]
> For remote debugging to work:
> * The *Debugger Mode* should be set to *Attach to remote JVM*.
> * You need to correctly specify the *Use module classpath* value. In general, use the `jvmMain` classpath associated
>   with your Kobweb application, e.g. `app.site.jvmMain`. If you've refactored your backend code out to another module,
>   you should be able to use that instead.

At this point, start up your Kobweb server using `kobweb run`.

> [!CAUTION]
> Remote debugging is only supported in dev mode. It will not be enabled for a server started with
> `kobweb run --env prod`.

With your Kobweb server running and your "remote debug" run configuration selected, press the debug button. If
everything is set up correctly, you should see a message in the IDE debugger console
like: `Connected to the target VM, address: 'localhost:5005', transport: 'socket'`

If instead, you see a red popup with a message
like `Unable to open debugger port (localhost:5005): java.net.ConnectException "Connection refused"`, please
double-check the values in your `conf.yaml` file, restart the server, and try again.
