package de.fbeutel.tweetalyzer.admin.model;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class GraphStatusResponse {

  private final long userNodeCount;
  private final long tweetNodeCount;

  private final long repliesToRelCount;
  private final long mentionsRelCount;
  private final long tweetsRelCount;
  private final long retweetsRelCount;
}
