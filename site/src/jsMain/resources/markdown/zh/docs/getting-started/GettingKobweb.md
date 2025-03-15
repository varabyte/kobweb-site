---
title: Kobweb安装
follows: Videos
---

开始使用 Kobweb 的第一步是获取 Kobweb 二进制文件。你可以通过安装、下载或构建的方式获取，
因此我们将介绍所有这些方法。

如果不确定选择哪种方式，我们建议通过我们支持的包管理器之一进行安装。

## 安装 Kobweb 二进制文件

### [Homebrew](https://brew.sh/)

*操作系统：Mac 和 Linux*

```bash
$ brew install varabyte/tap/kobweb
```

### [Scoop](https://scoop.sh/)

*操作系统：Windows*

```bash
# 注意：添加 bucket 只需要执行一次。

# 如果你已经安装了 java，可以跳过这步
> scoop bucket add java
> scoop install java/openjdk

# 安装 kobweb
> scoop bucket add varabyte https://github.com/varabyte/scoop-varabyte.git
> scoop install varabyte/kobweb
```

### [SDKMAN!](https://sdkman.io/)

*操作系统：Windows、Mac 和 \*nix*

```bash
$ sdk install kobweb
```

### Arch Linux

使用 [AUR 助手](https://wiki.archlinux.org/title/AUR_helpers)，例如：

```bash
$ yay -S kobweb
$ paru -S kobweb
$ trizen -S kobweb
# 等等
```

不使用 AUR 助手：

```bash
$ git clone https://aur.archlinux.org/kobweb.git
$ cd kobweb
$ makepkg -si
```

### 没有找到你喜欢的包管理器？

请访问 https://github.com/varabyte/kobweb-cli/issues/11 并考虑留下评论！

## 下载 Kobweb 二进制文件

我们的二进制文件托管在 GitHub 上。要下载最新版本，你可以
[从 GitHub 下载 zip 或 tar 文件](https://github.com/varabyte/kobweb-cli/releases/tag/v0.9.18)，
或者通过终端获取：

```bash
$ cd /path/to/applications

# 你可以下载 zip 文件

$ wget https://github.com/varabyte/kobweb-cli/releases/download/v0.9.18/kobweb-0.9.18.zip
$ unzip kobweb-0.9.18.zip

# ... 或者下载 tar 文件

$ wget https://github.com/varabyte/kobweb-cli/releases/download/v0.9.18/kobweb-0.9.18.tar
$ tar -xvf kobweb-0.9.18.tar
```

建议将其添加到你的环境变量中，可以直接添加：

```bash
$ PATH=$PATH:/path/to/applications/kobweb-0.9.18/bin
$ kobweb version # 检查是否正常工作
```

或通过符号链接：

```bash
$ cd /path/to/bin # 选择一个已在 PATH 中的文件夹
$ ln -s /path/to/applications/kobweb-0.9.18/bin/kobweb kobweb
```

## 构建 Kobweb 二进制文件

虽然我们在 GitHub 上托管 Kobweb 构建文件，但自己构建也很简单。

构建 Kobweb 需要 JDK11 或更新版本。我们首先讨论如何添加 JDK，如果你的机器上已经有了，可以跳过这一步。

### 下载 JDK

如果你想完全控制 JDK 安装，手动下载是个不错的选择。

* [为你的操作系统下载 JDK](https://docs.aws.amazon.com/corretto/latest/corretto-11-ug/downloads-list.html)
* 解压到某个位置
* 更新你的 `JAVA_HOME` 变量指向该位置。

```bash
JAVA_HOME=/path/to/jdks/corretto-11.0.12
# ... 或你选择的其他版本和路径
```

### 使用 IntelliJ IDE 安装 JDK

如果想要更自动化的方式，你可以让 IntelliJ 为你安装 JDK。

请参考他们的说明：https://www.jetbrains.com/help/idea/sdk.html#set-up-jdk

### 编译源代码

Kobweb CLI 实际上在一个独立的 GitHub 仓库中维护。一旦你设置好 JDK，克隆并构建它应该很容易：

```bash
$ cd /path/to/src/root # 选择一个存放源代码的文件夹
$ git clone https://github.com/varabyte/kobweb-cli
$ cd kobweb-cli
$ ./gradlew :kobweb:installDist
```

最后，更新你的 PATH：

```bash
$ PATH=$PATH:/path/to/src/root/kobweb-cli/kobweb/build/install/kobweb/bin
$ kobweb version # 检查是否正常工作
```

## 更新 Kobweb 二进制文件

如果你之前安装了 Kobweb 并且知道有新版本可用，更新方式取决于你的安装方式。

| 安装方式                | 更新说明                                                                                                            |
|------------------------|-------------------------------------------------------------------------------------------------------------------|
| Homebrew               | `brew upgrade kobweb`                                                                                               |
| Scoop                  | `scoop update kobweb`                                                                                               |
| SDKMAN!                | `sdk upgrade kobweb`                                                                                                |
| Arch Linux             | 重新运行[安装步骤](#arch-linux)应该可以。如果使用 AUR 助手，你可能需要查看其手册。                                   |
| 从 Github 下载          | 访问[最新发布版本](https://github.com/varabyte/kobweb-cli/releases/tag/v0.9.18)。那里有 zip 和 tar 文件可供下载。    |
