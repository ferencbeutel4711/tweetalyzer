package de.fbeutel.tweetalyzer.graph.domain;

import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface UserGraphRepository extends CrudRepository<User, Long> {

  Optional<User> findByRawId(final String rawId);

  @Query("MATCH p=()-[r:tweets]->() RETURN count(p)")
  Long countTweetsRels();

  @Query("MATCH p=()-[r:reTweets]->() RETURN count(p)")
  Long countReTweetsRels();

}
