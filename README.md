# tweetalyzer
The setup is tested under Ubuntu 18.04. Problems concerning the routing between the different docker containers over the host machine can arise under Windows or OSX. Using docker.host.internal as the docker host's hostname may help.

## Installation

* Install OpenJDK 11
* Install gradle
* Install NodeJS
* Install docker
* Move dataset to src/main/resources/data/german-tweet-sample-2019-04.zip

## Build project

```
gradle clean assemble
npm install
npm run build
```

## Run

### using docker-compose
```
docker-compose up
```

### using databases on the host machine

* Start your local mongoDB on the default port 27017 with no username and password configured.
* Start a new Neo4J Graph with the bolt port 7687 with a matching username and password in accordance to application.properties
* Start the application

```
gradle bootRun
```

## Accessing the frontend
The frontend is accessible under localhost:8080/. From there you can access all application features.
