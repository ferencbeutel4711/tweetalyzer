package de.fbeutel.tweetalyzer.graph.domain;

import java.util.List;
import java.util.Optional;

import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.repository.CrudRepository;

public interface UserGraphRepository extends CrudRepository<User, Long> {

  Optional<User> findByRawId(final String rawId);

  @Query("MATCH p=(n:Tweet)-[r*..10]-() WHERE n.hashTagSearchField CONTAINS {1} RETURN p LIMIT {0}")
  List<User> findForGraphWithHashtag(Integer limit, final String hashtagSearchCriteria);

  @Query("MATCH p=(n:User)-[r*..10]-() WHERE n.name={1} RETURN p LIMIT {0}")
  List<User> findForGraphWithUsername(Integer limit, final String usernameSearchCriteria);

  @Query("MATCH p=(n:User)-[r*..10]-(d:Tweet) WHERE n.name={2} AND d.hashTagSearchField CONTAINS {1} RETURN p LIMIT {0}")
  List<User> findForGraph(Integer limit, final String hashtagSearchCriteria, final String usernameSearchCriteria);

  @Query("MATCH (n)-[r*]->(d) RETURN n,r,d LIMIT {0}")
  List<User> findForGraph(Integer limit);

  @Query("MATCH p=()-[r:tweets]->() RETURN count(p)")
  Long countTweetsRels();

  @Query("MATCH p=()-[r:reTweets]->() RETURN count(p)")
  Long countReTweetsRels();
}
