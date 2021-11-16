FROM debian:stable-slim
USER root
# Install OpenJDK-11
RUN apt-get update && \
    apt-get install -y openjdk-11-jdk && \
    apt-get install -y ant && \
    apt-get clean;

# Fix certificate issues
RUN apt-get update && \
    apt-get install ca-certificates-java && \
    apt-get clean && \
    update-ca-certificates -f;

# Setup JAVA_HOME -- useful for docker commandline
ENV JAVA_HOME /usr/lib/jvm/java-11-openjdk-amd64/
RUN export JAVA_HOME
RUN java -version

RUN  apt-get update \
  && apt-get install -y wget unzip \
  && rm -rf /var/lib/apt/lists/* \

WORKDIR /app
COPY . /app
USER root
RUN pwd && ls

RUN wget https://github.com/varabyte/kobweb/releases/download/v0.6.3/kobweb-0.6.3.zip \
    && unzip kobweb-0.6.3.zip \
    && rm -r kobweb-0.6.3.zip

RUN cd /app && ls \
&& ./gradlew --stop

WORKDIR /app
CMD [ "../kobweb-0.6.3/bin/kobweb", "run"]
