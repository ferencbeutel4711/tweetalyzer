# tweetalyzer

## Installation

* Install MongoDB
* Install Neo4J
* Install NodeJS
* Install Java 11

## Build project

```
./gradlew build
./npm install
./npm build
```

## Run

* Start your local mongoDB on the default port 27017 with no username and password configured.
* Start a new Neo4J Graph with the bolt port 7687 with a matching username and password in accordance to application.properties
* Start the application

```
./gradlew bootRun
```

* Alternatively, import into IntelliJ 2019 as a gradle project and run through a springboot run configuration

## Providing data
Put the data which should be imported into src/main/resources/data. The expected filename is configured in the RawDataService as a constant for now.

## Starting the Import

```
curl localhost:8080/admin/startMongoImport
```

Please be aware that the import is running in parallel using up to 10 threads and might be taxing on your machine, depending on the available resources. Please adjust the ThreadCount in the RawDataService if needed.

## Accessing the frontend
The frontend is accessible under localhost:8080/. From there you can access all application features, including starting data imports/ transformations.
