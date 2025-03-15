---
title: "项目结构"
follows: ApplicationRoot
---

## 结构约定

在`pages`文件夹之外，通常会创建可重用的、可组合的部件。虽然Kobweb不强制执行任何特定规则，但我们推荐一个约定，如果遵循这个约定，可能会使新读者更容易理解你的代码库。

因此，作为`pages`的同级目录，创建一个名为`components`的文件夹。在其中添加：

* **layouts** - 提供完整页面布局的高级可组合组件。大多数（全部？）的`@Page`页面都会首先调用一个页面布局函数。你的整个网站可能只需要一个布局。
* **sections** - 中级可组合组件，代表页面内的复合区域，组织多个子可组合组件的集合。如果你有多个布局，sections很可能会在它们之间共享。例如，导航头部和页脚是这个子文件夹的理想候选。
* **widgets** - 低级可组合组件。专注于你可能想在整个网站中重用的UI部件。例如，一个样式化的访问计数器就是这个子文件夹的好选择。

你的项目应该看起来像这样：

{{{ Folders

* my-project
  * src/jsMain/kotlin
    * components
      * layouts
      * sections
      * widgets
    * pages

}}}

## 公共资源

如果你有想要从网站提供的资源，你可以通过将其放在网站的`jsMain/resources/public`文件夹中来处理。

例如，如果你有一个logo想要在`mysite.com/assets/images/logo.png`上可用，你需要将它放在Kobweb项目的`jsMain/resources/public/assets/images/logo.png`中。

{{{ Folders

* my-project
  * src/jsMain/resources
      * public
        * assets/images
          * logo.png

}}}

换句话说，项目资源中`public/`目录下的任何内容都会自动复制到最终网站中（但不包括`public/`部分）。

## 多模块项目

为了简单起见，大多数新项目会将所有页面和组件放在单个应用模块中，例如`site/`，在其构建脚本中应用`com.varabyte.kobweb.application`插件。

但是，你也可以在单独的模块中定义组件和/或页面。只需在它们的构建脚本中应用`com.varabyte.kobweb.library`插件即可。

换句话说，你可以像这样分割和组织你的项目：

{{{ Folders

* my-project
  * sitelib
    * build.gradle.kts
    * src/jsMain
      * kotlin/org/example/myproject/sitelib
        * components
        * pages
  * site
    * build.gradle.kts
    * .kobweb/conf.yaml
    * src/jsMain
      * kotlin/org/example/myproject/site
        * components
        * pages

}}}

如果你想探索多模块项目示例，可以通过运行以下命令：

```bash
$ kobweb create examples/chat
```

这演示了一个聊天应用程序，其认证和聊天功能分别组织在各自独立的模块中。
