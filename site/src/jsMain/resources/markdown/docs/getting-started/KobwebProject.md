---
title: Creating and Running a Kobweb Project
follows: GettingKobweb
---

A great way to check that Kobweb is installed correctly is by creating and running the default app project.

## Create the app project

Creating the default demo project is easy.

In a terminal, run the following commands:

```bash
$ cd /path/to/projects/
$ kobweb create app
```

You'll be asked a few questions required for setting up your project.

You don't need to create a root folder for your project ahead of time - the setup process will prompt you for one to
create. For the remaining parts of this section, let's say you choose the folder `"my-project"` when asked.

When finished, you'll have a basic project with two pages - a home page and an about page (with the about page written
in markdown) - and some components (which are collections of reusable, composable pieces). Your own directory structure
should look something like this:

{{{ Folders

* my-project
  * site/src/jsMain
    * kotlin/org/example/myproject
      * components
        * layouts
          * MarkdownLayout.kt
          * PageLayout.kt
        * sections
          * Footer.kt
          * NavHeader.kt
        * widgets
          * IconButton.kt
      * pages
        * Index.kt
      * AppEntry.kt
    * resources/markdown
      * About.md

}}}

Notice that there's no `index.html` or routing logic anywhere! We generate that for you automatically when you build
your Kobweb project.

## Run the default app site

```bash
$ cd my-project/site
$ kobweb run
```

This command spins up a web server at `http://localhost:8080`.

> [!TIP]
> If you want to configure the port, you can do so by editing your project's `.kobweb/conf.yaml` file. Most projects
> shouldn't care about this, but it could be useful if you are working on two related Kobweb sites at the same time.

At this point, you can open your project in IntelliJ and start editing it. While Kobweb is running, it will detect
changes in your source code, recompile, and deploy updates to your site automatically.

### Using IntelliJ

If you don't want to keep a separate terminal window open beside your IDE window, you may prefer to use solutions that
are integrated inside IntelliJ already.

#### Terminal tool window

You can use the [IntelliJ terminal tool window](https://www.jetbrains.com/help/idea/terminal-emulator.html) to run
`kobweb` within it. If you run into a compile error, the stack trace lines will get decorated with
links, making it easy to navigate to the relevant source.

#### Gradle commands

`kobweb` itself delegates to Gradle, but nothing is stopping you from calling the commands yourself. You can create
Gradle run configurations for each of the Kobweb commands.

> [!TIP]
> When you run a Kobweb CLI command that delegates to Gradle, it will log the Gradle command to the console. This is
> how you can discover the Gradle commands discussed in this section.

* To start a Kobweb server, use the `kobwebStart -t` command.
    * The `-t` argument (or, `--continuous`) tells Gradle to watch for file changes, which gives you live loading behavior.
* To stop a running Kobweb server, use the `kobwebStop` command.
* To export a site, use<br>
  `kobwebExport -PkobwebReuseServer=false -PkobwebEnv=DEV -PkobwebRunLayout=FULLSTACK -PkobwebBuildTarget=RELEASE -PkobwebExportLayout=FULLSTACK`
    * If you want to export a static layout instead, change the last argument to<br>`-PkobwebExportLayout=STATIC`.
* To run an exported site, use<br>
  `kobwebStart -PkobwebEnv=PROD -PkobwebRunLayout=FULLSTACK`
    * If your site was exported using a static layout, change the last argument to<br>`-PkobwebRunLayout=STATIC`.

You can read all about [IntelliJ's Gradle integration here](https://www.jetbrains.com/help/idea/gradle.html). Or to just jump straight into how to create run
configurations for any of the commands discussed above, read [these instructions](https://www.jetbrains.com/help/idea/run-debug-gradle.html).

## Other examples

Kobweb provides a growing collection of samples for you to learn from. To see what's available, run:

```bash
$ kobweb list

You can create the following Kobweb projects by typing `kobweb create ...`

• app: A template for a minimal site that demonstrates the basic features of Kobweb
• examples/jb/counter: A very minimal site with just a counter (based on the Jetbrains tutorial)
• examples/todo: An example TODO app, showcasing client / server interactions
```

For example, `kobweb create examples/todo` will instantiate a TODO app locally.