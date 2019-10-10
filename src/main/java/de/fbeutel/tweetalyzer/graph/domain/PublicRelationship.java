package de.fbeutel.tweetalyzer.graph.domain;

import lombok.Builder;
import lombok.Value;

@Value
@Builder(toBuilder = true)
public class PublicRelationship {

  private final RelationshipType type;

  private final String source;
  private final String target;
}
