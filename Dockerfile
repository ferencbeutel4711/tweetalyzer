FROM openjdk:11-jdk-slim

RUN echo "deb http://deb.debian.org/debian/ stretch main" | tee /etc/apt/sources.list.d/debian-stretch.list
RUN apt update && apt upgrade

COPY build/libs/tweetalyzer-1.jar /opt
