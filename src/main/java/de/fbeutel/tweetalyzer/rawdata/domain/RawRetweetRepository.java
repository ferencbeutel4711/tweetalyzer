package de.fbeutel.tweetalyzer.rawdata.domain;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.io.IOException;
import java.util.List;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import static org.springframework.data.util.StreamUtils.createStreamFromIterator;

public interface RawRetweetRepository extends RawRetweetRepositoryCustom, MongoRepository<RawRetweet, String> {

  List<RawRetweet> findAllByUserId(final String userId);

  List<RawRetweet> findAllByReference(final String reference);
}

interface RawRetweetRepositoryCustom {
  Stream<RawRetweet> findAllAsStream();
}

@Slf4j
class RawRetweetRepositoryImpl implements RawRetweetRepositoryCustom {
  private final MongoTemplate template;

  RawRetweetRepositoryImpl(MongoTemplate template) {
    this.template = template;
  }

  @Override
  public Stream<RawRetweet> findAllAsStream() {
    return createStreamFromIterator(template.stream(new Query().noCursorTimeout(), RawRetweet.class));
  }
}
