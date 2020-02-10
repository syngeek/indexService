FROM openjdk:8

ARG SBT_VERSION=1.3.7

RUN \
  curl -L -o sbt-$SBT_VERSION.deb https://dl.bintray.com/sbt/debian/sbt-$SBT_VERSION.deb && \
  dpkg -i sbt-$SBT_VERSION.deb && \
  rm sbt-$SBT_VERSION.deb && \
  apt-get update && \
  apt-get install sbt && \
  sbt sbtVersion

WORKDIR /yummly

ADD ./src src/
ADD ./target target/
ADD build.sbt /yummly/

RUN sbt clean compile

ENTRYPOINT ["sbt"]
CMD ["run"]