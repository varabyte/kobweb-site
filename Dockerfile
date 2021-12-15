FROM debian:stable-slim
USER root

# Copy the project code to app dir
COPY . /app

# Install OpenJDK-11 (earliest JDK kobweb can run on)
RUN apt-get update \
    && apt-get install -y openjdk-11-jdk \
    && apt-get install -y ant \
    && apt-get clean

# Fix certificate issues
RUN apt-get update \
    && apt-get install ca-certificates-java \
    && apt-get clean \
    && update-ca-certificates -f

# Setup JAVA_HOME -- needed by kobweb / gradle
ENV JAVA_HOME /usr/lib/jvm/java-11-openjdk-amd64/
RUN export JAVA_HOME
RUN java -version

# Add Chrome (for export)
RUN apt-get update \
    && apt-get install -y \
    apt-transport-https \
    ca-certificates \
    curl \
    gnupg \
    --no-install-recommends \
    && curl -sSL https://dl.google.com/linux/linux_signing_key.pub | apt-key add - \
    && echo "deb https://dl.google.com/linux/chrome/deb/ stable main" > /etc/apt/sources.list.d/google-chrome.list \
    && apt-get update && apt-get install -y \
    google-chrome-stable \
    fontconfig \
    fonts-ipafont-gothic \
    fonts-wqy-zenhei \
    fonts-thai-tlwg \
    fonts-kacst \
    fonts-symbola \
    fonts-noto \
    fonts-freefont-ttf \
    --no-install-recommends

# Install kobweb
RUN apt-get update && apt-get install -y wget unzip

RUN wget https://github.com/varabyte/kobweb/releases/download/v0.7.7/kobweb-0.7.7.zip \
    && unzip kobweb-0.7.7.zip \
    && rm -r kobweb-0.7.7.zip
ENV PATH="/kobweb-0.7.7/bin:${PATH}"
RUN echo $PATH

WORKDIR /app

RUN ./gradlew --stop && kobweb export --mode dumb

ENV PORT=8080
EXPOSE $PORT

# Purge all the things we don't need anymore

RUN apt-get purge --auto-remove -y curl gnupg wget unzip \
    && rm -rf /var/lib/apt/lists/*

# Keep container running because `kobweb run --mode dumb` doesn't block
CMD ./gradlew --stop && kobweb run --mode dumb --env prod && ./gradlew --stop && tail -f /dev/null
