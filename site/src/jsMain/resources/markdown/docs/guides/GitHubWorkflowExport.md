---
description: A guide showcasing an example GitHub workflow for exporting your site on the GitHub CI.
title: Exporting using GitHub Workflows
---

While you can always export your site manually on your machine, you may want to automate this process. A common
solution for this is a [GitHub workflow](https://docs.github.com/en/actions/using-workflows).

For your convenience, we include a sample workflow below that exports your site and then uploads the results (which can
be downloaded from a link shown in the workflow summary page):

```yaml 4,27-29,31-34,36-42,61-68 ".github/workflows/export-site.yml"
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
      KOBWEB_CLI_VERSION: 0.9.21

    steps:
      - uses: actions/checkout@v4
      - uses: actions/setup-java@v4
        with:
          distribution: temurin
          java-version: 11

      # When projects are created on Windows, the executable bit is sometimes lost. So set it back just in case.
      - name: Ensure Gradle is executable
        run: chmod +x gradlew

      # B  
      - name: Setup Gradle
        uses: gradle/actions/setup-gradle@v3

      # C
      - name: Query Browser Cache ID
        id: browser-cache-id
        run: echo "value=$(./gradlew -q :site:kobwebBrowserCacheId)" >> $GITHUB_OUTPUT

      # Also C
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

You can copy this workflow into your own GitHub project and then modify it to your needs.

We tagged some of the workflow code above with lettered comments (`#A`, `#B`, ...). Here are some additional notes about
those sections:

* ***(A) `workflow_dispatch`:*** This means that you can manually trigger this workflow from the GitHub UI, which I
  suggested here to prevent running a potentially expensive export operation without your direct involvement. Of course,
  you can also configure your workflow to run on a schedule, or on push to a branch, etc. Please refer to
  the [relevant GitHub docs](https://docs.github.com/en/actions/writing-workflows/choosing-when-your-workflow-runs/events-that-trigger-workflows)
  for a full list of events you can use.
* ***(B) Setup Gradle:*** This action is optional but I recommend it because it configures a bunch of caching for you.
* ***(C) Caching the browser:*** `kobweb export` needs to download a browser the first time it is run. This workflow sets up
  a cache that saves it across runs. The cache is tagged with a unique ID tied to the current browser version used by
  Kobweb. If this ever changes in a future release, GitHub will be instructed to use a new cache bucket (allowing
  GitHub to eventually clean up the old one).
* ***(D) Upload site:*** This action uploads the exported site as an artifact. You can then download the artifact from the
  workflow summary page. Your own workflow will likely delete this action and do something else here, like upload to a
  web server (or some location accessible by your web server) or copy files over into a `gh_pages` repository. I've
  included this here (and set the retention days very low) just so you can verify that the workflow is working for your
  project.

For a simple site, the above workflow should take about 2 minutes to run.
