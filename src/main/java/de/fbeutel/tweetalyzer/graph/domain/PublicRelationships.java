package de.fbeutel.tweetalyzer.graph.domain;

import lombok.Builder;
import lombok.Value;

import java.util.HashSet;
import java.util.Set;

@Value
@Builder(toBuilder = true)
public class PublicRelationships {

  private final Set<PublicRelationship> tweets;
  private final Set<PublicRelationship> reTweets;
  private final Set<PublicRelationship> mentions;
  private final Set<PublicRelationship> replies;

  public static PublicRelationships empty() {
    return PublicRelationships.builder()
            .tweets(new HashSet<>())
            .reTweets(new HashSet<>())
            .mentions(new HashSet<>())
            .replies(new HashSet<>())
            .build();
  }
}
