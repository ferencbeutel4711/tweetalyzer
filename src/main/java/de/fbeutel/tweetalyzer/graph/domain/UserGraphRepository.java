package de.fbeutel.tweetalyzer.graph.domain;

import java.util.List;
import java.util.Optional;

import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.repository.CrudRepository;

public interface UserGraphRepository extends CrudRepository<User, Long> {

  Optional<User> findByRawId(final String rawId);

  @Query("MATCH (n)-[r*]->(d) WHERE n.text=~{1} OR d.text=~{1} RETURN n,r,d LIMIT {0}")
  List<User> findForGraphWithHashtag(Integer limit, final String hashtagSearchCriteria);

  @Query("MATCH (n)-[r*]->(d) WHERE n.name={1} OR d.name={1} RETURN n,r,d LIMIT {0}")
  List<User> findForGraphWithUsername(Integer limit, final String usernameSearchCriteria);

  @Query("MATCH (n)-[r*]->(d) WHERE n.name ={2} OR d.name={2} WITH * WHERE n.text=~{1} OR d.text=~{1} RETURN n,r,d LIMIT {0}")
  List<User> findForGraph(Integer limit, final String hashtagSearchCriteria, final String usernameSearchCriteria);

  @Query("MATCH (n)-[r*]->(d) RETURN n,r,d LIMIT {0}")
  List<User> findForGraph(Integer limit);

  @Query("MATCH p=()-[r:tweets]->() RETURN count(p)")
  Long countTweetsRels();

  @Query("MATCH p=()-[r:reTweets]->() RETURN count(p)")
  Long countReTweetsRels();
}
