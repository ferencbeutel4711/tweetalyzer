package de.fbeutel.tweetalyzer.graph.domain;

import lombok.Builder;
import lombok.Value;

import java.util.Set;

@Value
@Builder(toBuilder = true)
public class PublicNetwork {

  private final PublicNodes nodes;
  private final PublicRelationships relationships;
}
