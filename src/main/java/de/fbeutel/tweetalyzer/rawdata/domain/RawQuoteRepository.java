package de.fbeutel.tweetalyzer.rawdata.domain;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.stream.Stream;

import static org.springframework.data.util.StreamUtils.createStreamFromIterator;

public interface RawQuoteRepository extends RawQuoteRepositoryCustom, MongoRepository<RawQuote, String> {
    List<RawQuote> findAllByReference(final String reference);
}

interface RawQuoteRepositoryCustom {
    Stream<RawQuote> findAllAsStream();
}

@Slf4j
class RawQuoteRepositoryImpl implements RawQuoteRepositoryCustom {
    private final MongoTemplate template;

    RawQuoteRepositoryImpl(MongoTemplate template) {
        this.template = template;
    }

    @Override
    public Stream<RawQuote> findAllAsStream() {
        return createStreamFromIterator(template.stream(new Query().noCursorTimeout(), RawQuote.class));
    }
}
