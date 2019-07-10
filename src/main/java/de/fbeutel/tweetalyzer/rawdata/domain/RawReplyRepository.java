package de.fbeutel.tweetalyzer.rawdata.domain;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.stream.Stream;

import static org.springframework.data.util.StreamUtils.createStreamFromIterator;

public interface RawReplyRepository extends RawReplyRepositoryCustom, MongoRepository<RawReply, String> {
}

interface RawReplyRepositoryCustom {
  Stream<RawReply> findAllAsStream();
}

@Slf4j
class RawReplyRepositoryImpl implements RawReplyRepositoryCustom {
  private final MongoTemplate template;

  RawReplyRepositoryImpl(MongoTemplate template) {
    this.template = template;
  }

  @Override
  public Stream<RawReply> findAllAsStream() {
    return createStreamFromIterator(template.stream(new Query().noCursorTimeout(), RawReply.class));
  }
}
