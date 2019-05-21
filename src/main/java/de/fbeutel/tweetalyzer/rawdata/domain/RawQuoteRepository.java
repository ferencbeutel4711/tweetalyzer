package de.fbeutel.tweetalyzer.rawdata.domain;

import org.springframework.data.mongodb.repository.MongoRepository;

public interface RawQuoteRepository extends MongoRepository<RawQuote, String> {
}
