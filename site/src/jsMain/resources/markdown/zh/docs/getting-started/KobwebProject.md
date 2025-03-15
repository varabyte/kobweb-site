---
title: Kobweb项目创建及运行
follows: GettingKobweb
---

检验 Kobweb 是否正确安装的好方法是创建并运行默认的应用程序项目。

## 创建应用程序项目

创建默认演示项目非常简单。

在终端中运行以下命令：

```bash
$ cd /path/to/projects/
$ kobweb create app
```

您将被问及一些设置项目所需的问题。

您不需要提前为项目创建根文件夹 - 设置过程会提示您创建一个。在本节的其余部分中，假设当被问及时您选择了文件夹 `"my-project"`。

完成后，您将得到一个包含两个页面的基本项目 - 一个主页和一个关于页面（关于页面用 markdown 编写）- 以及一些组件（可重用、可组合的代码片段的集合）。您的目录结构应该如下所示：

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

注意，这里没有 `index.html` 或路由逻辑！当您构建 Kobweb 项目时，我们会自动为您生成这些内容。

## 运行默认的应用站点

```bash
$ cd my-project/site
$ kobweb run
```

此命令会在 `http://localhost:8080` 启动一个 Web 服务器。

> [!TIP]
> 如果您想配置端口，可以通过编辑项目的 `.kobweb/conf.yaml` 文件来实现。大多数项目不需要关心这个，但如果您同时在处理两个相关的 Kobweb 站点，这可能会很有用。

此时，您可以在 IntelliJ 中打开项目并开始编辑。当 Kobweb 运行时，它会检测源代码的变化，重新编译，并自动部署更新到您的站点。

### 使用 IntelliJ

如果您不想在 IDE 窗口旁边保持一个单独的终端窗口，您可能更喜欢使用已经集成在 IntelliJ 中的解决方案。

#### 终端工具窗口

您可以使用 [IntelliJ 终端工具窗口](https://www.jetbrains.com/help/idea/terminal-emulator.html) 在其中运行 `kobweb`。如果遇到编译错误，堆栈跟踪行会被装饰上链接，方便导航到相关源代码。

#### Gradle 命令

`kobweb` 本身会委托给 Gradle，但没有什么能阻止您自己调用这些命令。您可以为每个 Kobweb 命令创建 Gradle 运行配置。

> [!TIP]
> 当您运行委托给 Gradle 的 Kobweb CLI 命令时，它会将 Gradle 命令记录到控制台。这就是您可以发现本节讨论的 Gradle 命令的方式。

* 要启动 Kobweb 服务器，使用 `kobwebStart -t` 命令。
    * `-t` 参数（或 `--continuous`）告诉 Gradle 监视文件变化，这提供了实时加载行为。
* 要停止运行中的 Kobweb 服务器，使用 `kobwebStop` 命令。
* 要导出站点，使用<br>
  `kobwebExport -PkobwebReuseServer=false -PkobwebEnv=DEV -PkobwebRunLayout=FULLSTACK -PkobwebBuildTarget=RELEASE -PkobwebExportLayout=FULLSTACK`
    * 如果您想导出静态布局，将最后一个参数改为<br>`-PkobwebExportLayout=STATIC`。
* 要运行导出的站点，使用<br>
  `kobwebStart -PkobwebEnv=PROD -PkobwebRunLayout=FULLSTACK`
    * 如果您的站点使用静态布局导出，将最后一个参数改为<br>`-PkobwebRunLayout=STATIC`。

您可以在[这里阅读所有关于 IntelliJ 的 Gradle 集成](https://www.jetbrains.com/help/idea/gradle.html)。或者直接跳转到如何为上述任何命令创建运行配置，请阅读[这些说明](https://www.jetbrains.com/help/idea/run-debug-gradle.html)。

## 其他示例

Kobweb 提供了越来越多的示例供您学习。要查看有哪些可用，运行：

```bash
$ kobweb list

您可以通过输入 `kobweb create ...` 创建以下 Kobweb 项目

• app: 演示 Kobweb 基本功能的最小站点模板
• examples/jb/counter: 仅包含计数器的非常简单的站点（基于 Jetbrains 教程）
• examples/todo: 一个 TODO 应用示例，展示客户端/服务器交互
```

例如，`kobweb create examples/todo` 将在本地实例化一个 TODO 应用。