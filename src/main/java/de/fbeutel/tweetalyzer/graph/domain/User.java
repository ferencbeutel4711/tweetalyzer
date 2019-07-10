package de.fbeutel.tweetalyzer.graph.domain;

import de.fbeutel.tweetalyzer.rawdata.domain.RawUser;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.neo4j.ogm.annotation.*;

import java.util.HashSet;
import java.util.Set;

import static org.neo4j.ogm.annotation.Relationship.UNDIRECTED;

@Data
@NodeEntity
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class User {

  @Id
  @GeneratedValue
  private Long id;

  @Index(unique = true)
  private String rawId;
  private String name;

  @Relationship(type = "tweets", direction = UNDIRECTED)
  private Set<Tweet> createdTweets;

  @Relationship(type = "reTweets", direction = UNDIRECTED)
  private Set<Tweet> reTweets;

  public static User fromRawData(final RawUser rawUser) {
    return User.builder()
            .rawId(rawUser.getId())
            .name(rawUser.getScreenName())
            .createdTweets(new HashSet<>())
            .reTweets(new HashSet<>())
            .build();
  }

  public void addTweet(final Tweet tweet) {
    if (this.createdTweets == null) {
      this.createdTweets = new HashSet<>();
    }

    this.createdTweets.add(tweet);
  }

  public void addReTweet(final Tweet tweet) {
    if (this.reTweets == null) {
      this.reTweets = new HashSet<>();
    }

    this.reTweets.add(tweet);
  }
}
