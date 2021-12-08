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

# Install utils- wget and unzip
RUN  apt-get update \
  && apt-get install -y wget unzip \
  && rm -rf /var/lib/apt/lists/* \

WORKDIR /app

# Copy the project code to app dir
COPY . /app
USER root
RUN pwd && ls

# Install kobweb
RUN wget https://github.com/varabyte/kobweb/releases/download/v0.7.4/kobweb-0.7.4.zip \
    && unzip kobweb-0.7.4.zip \
    && rm -r kobweb-0.7.4.zip

RUN cd /app && ./gradlew --stop

WORKDIR /app

ENV PORT=8080
EXPOSE $PORT
CMD [ "../kobweb-0.7.4/bin/kobweb", "run", "--env", "prod"]
