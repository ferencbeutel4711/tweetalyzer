package de.fbeutel.tweetalyzer.graph.domain;

import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

public interface UserGraphRepository extends CrudRepository<User, Long> {

  Optional<User> findByRawId(final String rawId);

  @Query("MATCH (n)-[r*]->(d) RETURN n,r,d LIMIT {0}")
  List<User> findForGraph(Integer limit);

  @Query("MATCH p=()-[r:tweets]->() RETURN count(p)")
  Long countTweetsRels();

  @Query("MATCH p=()-[r:reTweets]->() RETURN count(p)")
  Long countReTweetsRels();

}
