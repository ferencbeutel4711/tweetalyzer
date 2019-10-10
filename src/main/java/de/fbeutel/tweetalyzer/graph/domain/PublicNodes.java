package de.fbeutel.tweetalyzer.graph.domain;

import lombok.Builder;
import lombok.Value;

import java.util.HashSet;
import java.util.Set;

@Value
@Builder(toBuilder = true)
public class PublicNodes {

  private final Set<PublicUser> users;
  private final Set<PublicTweet> tweets;

  public static PublicNodes empty() {
    return PublicNodes.builder()
            .users(new HashSet<>())
            .tweets(new HashSet<>())
            .build();
  }
}
