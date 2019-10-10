package de.fbeutel.tweetalyzer.graph.domain;

import lombok.Builder;
import lombok.Value;

import java.util.Set;

@Value
@Builder(toBuilder = true)
public class PublicUser {

  private final NodeType type;

  private final String id;
  private final String name;
}
