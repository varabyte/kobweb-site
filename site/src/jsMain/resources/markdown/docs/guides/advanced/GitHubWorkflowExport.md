---
title: Exporting using GitHub Workflows
follows: ExistingProject
---

While you can always export your site manually on your machine, you may want to automate this process. A common
solution for this is a [GitHub workflow](https://docs.github.com/en/actions/using-workflows).

For your convenience, we include a sample workflow below that exports your site and then uploads the results (which can
be downloaded from a link shown in the workflow summary page):

```yaml
# .github/workflows/export-site.yml

name: Export Kobweb site

on:
  workflow_dispatch:

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

      # When projects are created on Windows, the executable bit is sometimes lost. So set it back just in case.
      - name: Ensure Gradle is executable
        run: chmod +x gradlew

      - name: Setup Gradle
        uses: gradle/actions/setup-gradle@v3

      - name: Query Browser Cache ID
        id: browser-cache-id
        run: echo "value=$(./gradlew -q :site:kobwebBrowserCacheId)" >> $GITHUB_OUTPUT

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

      - name: Upload site
        uses: actions/upload-artifact@v4
        with:
          name: site
          path: site/.kobweb/site/
          if-no-files-found: error
          retention-days: 1
```

You can copy this workflow (or parts of it) into your own GitHub project and then modify it to your needs.

Some notes...

* ***workflow_dispatch***: This means that you can manually trigger this workflow from the GitHub UI, which I
  suggested here to prevent running an expensive export operation more than you need to. Of course, you can also
  configure your workflow to run on a schedule, or on push to a branch, etc.
* ***Setup Gradle***: This action is optional but I recommend it because it configures a bunch of caching for you.
* ***Caching the browser***: `kobweb export` needs to download a browser the first time it is run. This workflow sets up
  a cache that saves it across runs. The cache is tagged with a unique ID so that future Kobweb releases, which may
  change the version of the browser downloaded, will use a new cache bucket (allowing GitHub to eventually clean up the old
  one).
* ***Upload site***: This action uploads the exported site as an artifact. You can then download the artifact from the
  workflow summary page. Your own workflow will likely delete this action and do something else here, like upload to a
  web server (or some location accessible by your web server) or copy files over into a `gh_pages` repository. I've
  included this here (and set the retention days very low) just so you can verify that the workflow is working for your
  project.

For a simple site, the above workflow should take about 2 minutes to run.
