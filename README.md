# tweetalyzer

## Installation

* Install MongoDB
* Install Java 11
* Build project

```
./gradlew build
```

## Run

* Start your local mongoDB on the default port 27017 with no username and password configured.
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
