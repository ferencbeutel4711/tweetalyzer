# tweetalyzer

## Installation

* Install MongoDB
* Install Neo4J
* Install NodeJS
* Install OpenJDK 11
* Install docker
* Provide dataset to src/main/resources/data/german-tweet-sample-2019-04.zip

## Build project

```
./gradlew build
./npm install
./npm build
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
./gradlew bootRun
```

## Accessing the frontend
The frontend is accessible under localhost:8080/. From there you can access all application features.
