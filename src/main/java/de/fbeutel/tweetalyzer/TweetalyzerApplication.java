package de.fbeutel.tweetalyzer;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import org.springframework.data.neo4j.repository.config.EnableNeo4jRepositories;

@SpringBootApplication
@EnableNeo4jRepositories
@EnableMongoRepositories
public class TweetalyzerApplication {

  public static void main(String[] args) {
    SpringApplication.run(TweetalyzerApplication.class, args);
  }

}
