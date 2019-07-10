package de.fbeutel.tweetalyzer.rawdata.domain;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.io.IOException;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import static org.springframework.data.util.StreamUtils.createStreamFromIterator;

public interface RawUserRepository extends RawUserRepositoryCustom, MongoRepository<RawUser, String> {
}

interface RawUserRepositoryCustom {
  Stream<RawUser> findAllAsStream();
}

@Slf4j
class RawUserRepositoryImpl implements RawUserRepositoryCustom {
  private final MongoTemplate template;

  RawUserRepositoryImpl(MongoTemplate template) {
    this.template = template;
  }

  @Override
  public Stream<RawUser> findAllAsStream() {
    return createStreamFromIterator(template.stream(new Query().noCursorTimeout(), RawUser.class));
  }
}
