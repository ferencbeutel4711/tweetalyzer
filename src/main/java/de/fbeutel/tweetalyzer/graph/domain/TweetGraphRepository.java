package de.fbeutel.tweetalyzer.graph.domain;

import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

public interface TweetGraphRepository extends CrudRepository<Tweet, Long> {

  List<Tweet> findAllByUserId(final String userId);

  Optional<Tweet> findByRawId(final String rawId);

  Optional<Tweet> findByReplyTargetId(final String replyTargetId);

  List<Tweet> findAllByMentionedIdsContaining(String userId);

  @Query("MATCH p=()-[r:replies_to]->() RETURN count(p)")
  Long countRepliesToRels();

  @Query("MATCH p=()-[r:mentions]->() RETURN count(p)")
  Long countMentionsRels();
}