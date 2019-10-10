package de.fbeutel.tweetalyzer.graph.domain;

import lombok.Builder;
import lombok.Value;

@Value
@Builder(toBuilder = true)
public class PublicTweet {

  private final NodeType type;

  private final String id;
  private final String text;
}
