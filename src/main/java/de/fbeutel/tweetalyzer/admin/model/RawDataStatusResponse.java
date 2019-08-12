package de.fbeutel.tweetalyzer.admin.model;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class RawDataStatusResponse {

  private final long userCount;
  private final long statusCount;
  private final long retweetCount;
  private final long replyCount;
  private final long quoteCount;
}
