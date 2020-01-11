package de.fbeutel.tweetalyzer.graph.domain;

import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

public interface TweetGraphRepository extends CrudRepository<Tweet, Long> {

    List<Tweet> findAllByUserId(final String userId);

    Optional<Tweet> findByRawId(final String rawId);

    Optional<Tweet> findByReplyTargetId(final String replyTargetId);

    Optional<Tweet> findByQuoteTargetId(final String quoteTargetId);

    List<Tweet> findAllByMentionedIdsContaining(String userId);

    @Query("MATCH p=()-[r:replies_to]->() RETURN count(p)")
    Long countRepliesToRels();

    @Query("MATCH p=()-[r:mentions]->() RETURN count(p)")
    Long countMentionsRels();

    @Query("MATCH p=()-[r:quotes]->() RETURN count(p)")
    Long countQuoteRels();
}