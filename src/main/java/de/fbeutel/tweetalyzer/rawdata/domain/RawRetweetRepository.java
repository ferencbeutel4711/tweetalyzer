package de.fbeutel.tweetalyzer.rawdata.domain;

import org.springframework.data.mongodb.repository.MongoRepository;

public interface RawRetweetRepository extends MongoRepository<RawRetweet, String> {
}
