---
title: 使用 GitHub Workflows 导出
follows: GeneratingCode
---

虽然你可以随时在本地机器上手动导出你的网站，但你可能想要自动化这个过程。一个常见的解决方案是使用 
[GitHub workflow](https://docs.github.com/en/actions/using-workflows)。

为了方便起见，我们在下面提供了一个示例工作流，它可以导出你的网站并上传结果（可以从工作流摘要页面中的链接下载）：

```yaml
# .github/workflows/export-site.yml

name: Export Kobweb site

on:
  workflow_dispatch: #A

jobs:
  export_and_upload:
    runs-on: ubuntu-latest
    defaults:
      run:
        shell: bash

    env:
      KOBWEB_CLI_VERSION: 0.9.18

    steps:
      - uses: actions/checkout@v4
      - uses: actions/setup-java@v4
        with:
          distribution: temurin
          java-version: 11

      # 当项目在 Windows 上创建时，可执行位有时会丢失。所以为了以防万一要重新设置。
      - name: Ensure Gradle is executable
        run: chmod +x gradlew

      # B  
      - name: Setup Gradle
        uses: gradle/actions/setup-gradle@v3

      # C
      - name: Query Browser Cache ID
        id: browser-cache-id
        run: echo "value=$(./gradlew -q :site:kobwebBrowserCacheId)" >> $GITHUB_OUTPUT

      # 也是 C
      - name: Cache Browser Dependencies
        uses: actions/cache@v4
        id: playwright-cache
        with:
          path: ~/.cache/ms-playwright
          key: ${{ runner.os }}-playwright-${{ steps.browser-cache-id.outputs.value }}

      - name: Fetch kobweb
        uses: robinraju/release-downloader@v1.9
        with:
          repository: "varabyte/kobweb-cli"
          tag: "v${{ env.KOBWEB_CLI_VERSION }}"
          fileName: "kobweb-${{ env.KOBWEB_CLI_VERSION }}.zip"
          tarBall: false
          zipBall: false

      - name: Unzip kobweb
        run: unzip kobweb-${{ env.KOBWEB_CLI_VERSION }}.zip

      - name: Run export
        run: |
          cd site
          ../kobweb-${{ env.KOBWEB_CLI_VERSION }}/bin/kobweb export --notty --layout static

      # D
      - name: Upload site
        uses: actions/upload-artifact@v4
        with:
          name: site
          path: site/.kobweb/site/
          if-no-files-found: error
          retention-days: 1
```

你可以将此工作流复制到你自己的 GitHub 项目中，然后根据需要修改它。

我们在上面的工作流代码中用字母注释标记了一些部分（`#A`、`#B`、...）。以下是关于这些部分的一些补充说明：

* ***(A) `workflow_dispatch`:*** 这意味着你可以从 GitHub UI 手动触发此工作流。我在这里建议这样做是为了防止在没有你直接参与的情况下运行可能比较耗费资源的导出操作。
  当然，你也可以配置工作流按计划运行，或在推送到分支时运行等。请参考 
  [相关的 GitHub 文档](https://docs.github.com/en/actions/writing-workflows/choosing-when-your-workflow-runs/events-that-trigger-workflows)
  以获取完整的可用事件列表。
* ***(B) 设置 Gradle:*** 这个操作是可选的，但我建议使用它，因为它为你配置了大量的缓存。
* ***(C) 缓存浏览器:*** `kobweb export` 在首次运行时需要下载浏览器。这个工作流设置了一个缓存来在多次运行之间保存它。缓存使用与当前 Kobweb 使用的浏览器版本相关联的唯一 ID 
  进行标记。如果将来的版本中这个发生变化，GitHub 将被指示使用新的缓存桶（允许 GitHub 最终清理旧的缓存）。
* ***(D) 上传网站:*** 这个操作将导出的网站作为构件上传。然后你可以从工作流摘要页面下载这个构件。你自己的工作流很可能会删除这个操作，而是做些其他事情，比如上传到网络服务器（或网络服务器可访问的某个位置）或将文件复制到 `gh_pages` 
  仓库。我在这里包含这个操作（并将保留天数设置得很低）只是为了让你可以验证工作流对你的项目是否正常工作。

对于一个简单的网站，上述工作流大约需要 2 分钟才能运行完成。
