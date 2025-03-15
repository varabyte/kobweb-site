---
title: 将 Kobweb 添加到现有项目中
follows: SharingDataObjects
---

目前，Kobweb 仍处于积极开发阶段，由于资源有限，我们主要专注于改进从零开始创建新项目的流程。然而，一些用户对 Kobweb 
表示了兴趣，但他们已经有了现有的项目，不确定如何将 Kobweb 集成进去。

只要你明白这种方式目前还没有得到官方支持，我们将在下面提供一些步骤，这些步骤可以帮助人们暂时手动完成这个过程。
说实话，最困难的部分是创建正确的 `.kobweb/conf.yaml`，以下步骤将帮助你解决这个问题：

1. 请务必查看 Kobweb 兼容性矩阵 [(COMPATIBILITY.md)](https://github.com/varabyte/kobweb/blob/main/COMPATIBILITY.md)，
   以确保你可以匹配它所期望的版本。
2. 在系统的某个位置创建一个临时应用项目。注意它询问的问题，因为你可能想要选择与你的项目匹配的包名。
   ```bash
   # 在某个临时目录中
   kobweb create app
   # 或者使用 `kobweb create app/empty`，如果你已经
   # 有 Kobweb 使用经验并且知道自己在做什么
   ```
3. 完成后，将 `site` 子文件夹复制到你自己的项目中。（完成后，你可以删除临时项目，因为它已经完成了它的使命。）
   ```bash
   $ cp -r app/site /path/to/your/project
   # 然后 `rm -rf app`
   ```
4. 确保你的 Gradle 项目配置包含 Maven Central 和 Gradle Plugin Portal 仓库，
   如 ${DocsLink("Kobweb 构件仓库", "/kobweb/getting-started/gradle-and-maven-artifacts#kobweb-artifact-repositories")}中所述。
5. Kobweb 使用 ${DocsLink("Gradle 版本目录", "/kobweb/getting-started/gradle-and-maven-artifacts#gradle-version-catalogs")}
   来管理依赖。在 `gradle/libs.versions.toml` 中添加或更新你的版本目录，包含以下内容：
   ```toml
   [versions]
   jetbrains-compose = "..." # 替换为实际版本，参见 COMPATIBILITY.md！
   kobweb = "..." # 替换为实际版本
   kotlin = "..." # 替换为实际版本

   [libraries]
   kobweb-api = { module = "com.varabyte.kobweb:kobweb-api", version.ref = "kobweb" }
   kobweb-core = { module = "com.varabyte.kobweb:kobweb-core ", version.ref = "kobweb" }
   kobweb-silk = { module = "com.varabyte.kobweb:kobweb-silk", version.ref = "kobweb" }
   kobwebx-markdown = { module = "com.varabyte.kobwebx:kobwebx-markdown", version.ref = "kobweb" }
   silk-icons-fa = { module = "com.varabyte.kobwebx:silk-icons-fa", version.ref = "kobweb" }

   [plugins]
   jetbrains-compose = { id = "org.jetbrains.compose", version.ref = "jetbrains-compose" }
   kobweb-application = { id = "com.varabyte.kobweb.application", version.ref = "kobweb" }
   kobwebx-markdown = { id = "com.varabyte.kobwebx.markdown", version.ref = "kobweb" }
   kotlin-multiplatform = { id = "org.jetbrains.kotlin.multiplatform", version.ref = "kotlin" }
   ```

如果一切正常，你现在应该能够在项目中运行 Kobweb 了：

```bash
# 在 /path/to/your/project 中
cd site
kobweb run
```
