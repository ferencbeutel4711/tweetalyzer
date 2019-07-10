package de.fbeutel.tweetalyzer.rawdata.domain;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.util.StreamUtils;

import java.io.IOException;
import java.util.List;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import static org.springframework.data.util.StreamUtils.createStreamFromIterator;

public interface RawStatusRepository extends RawStatusRepositoryCustom, MongoRepository<RawStatus, String> {

  List<RawStatus> findAllByUserId(final String userId);
}

interface RawStatusRepositoryCustom {
  Stream<RawStatus> findAllAsStream();
}

@Slf4j
class RawStatusRepositoryImpl implements RawStatusRepositoryCustom {
  private final MongoTemplate template;

  RawStatusRepositoryImpl(MongoTemplate template) {
    this.template = template;
  }

  @Override
  public Stream<RawStatus> findAllAsStream() {
    return createStreamFromIterator(template.stream(new Query().noCursorTimeout(), RawStatus.class));
  }
}
