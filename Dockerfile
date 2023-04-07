FROM eclipse-temurin:11-jdk-focal as builder
USER root

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
RUN curl -O -L https://github.com/varabyte/kobweb/releases/download/cli-v0.9.7/kobweb-0.9.7.tar \
    && tar xf kobweb-0.9.7.tar \
    && rm -r kobweb-0.9.7.tar
ENV PATH="/kobweb-0.9.7/bin:${PATH}"

# Copy the project code to app dir
COPY . /app

WORKDIR /app

RUN ./gradlew --stop && kobweb export --mode dumb

FROM eclipse-temurin:11-jre-focal
USER root

# Install kobweb
RUN curl -O -L https://github.com/varabyte/kobweb/releases/download/cli-v0.9.7/kobweb-0.9.7.tar \
    && tar xf kobweb-0.9.7.tar \
    && rm -r kobweb-0.9.7.tar
ENV PATH="/kobweb-0.9.7/bin:${PATH}"

# copy exported project code from builder image
COPY --from=builder /app /app
WORKDIR /app

ENV PORT=8080
EXPOSE $PORT

# preload the gradle binary
RUN ./gradlew --stop

# Keep container running because `kobweb run --mode dumb` doesn't block
CMD kobweb run --mode dumb --env prod && tail -f /dev/null
