FROM debian:stable-slim
USER root

ARG KOBWEB_CLI_VERSION=0.9.12

# Copy the project code to an arbitrary subdir so we can install stuff in the
# Docker container root without worrying about clobbering project files.
COPY . /project

# Prepare apt-get and get generally useful packages
RUN apt-get update \
    && apt-get install -y curl gnupg unzip wget

# Prepare npm and use it to initialize the browser we'll use for exporting
# (installed by playwright)
RUN curl -sL https://deb.nodesource.com/setup_19.x | bash - \
    && apt-get install -y nodejs \
    && npm init -y \
    && npx playwright install --with-deps chromium

# Install OpenJDK-11 (earliest JDK kobweb can run on)
RUN apt-get install -y openjdk-11-jdk

# Setup JAVA_HOME -- needed by kobweb / gradle
ENV JAVA_HOME /usr/lib/jvm/java-11-openjdk-amd64/
RUN export JAVA_HOME
RUN java -version

# Install kobweb
RUN wget https://github.com/varabyte/kobweb-cli/releases/download/v${KOBWEB_CLI_VERSION}/kobweb-${KOBWEB_CLI_VERSION}.zip \
    && unzip kobweb-${KOBWEB_CLI_VERSION}.zip \
    && rm -r kobweb-${KOBWEB_CLI_VERSION}.zip
ENV PATH="/kobweb-${KOBWEB_CLI_VERSION}/bin:${PATH}"
RUN echo $PATH

# Decrease Gradle memory usage to avoid OOM situations in tight environments.
RUN touch ~/gradle.properties
RUN echo "org.gradle.jvmargs=-Xmx256m" >> ~/gradle.properties

WORKDIR /project

RUN kobweb export --notty

RUN export PORT=$(kobweb conf server.port)
EXPOSE $PORT

# Purge all the things we don't need anymore, saving space on web servers that
# may need all the spare MB they can get!
RUN apt-get clean && apt-get purge --auto-remove -y curl gnupg nodejs unzip wget  \
    && rm -rf /var/lib/apt/lists/* \
    && rm -rf ~/.cache/ms-playwright

# Finally, run our web server! We stop the Gradle daemon at this point because
# its work is done. Finally, we keep the Docker container from closing (with
# `tail -f ...`) since `kobweb run --notty` doesn't block but is running in the
# background.
CMD kobweb run --notty --env prod \
  && ./gradlew --stop \
  && tail -f /dev/null
