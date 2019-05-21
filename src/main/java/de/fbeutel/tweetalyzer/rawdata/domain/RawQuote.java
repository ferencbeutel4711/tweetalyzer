package de.fbeutel.tweetalyzer.rawdata.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Value;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;
import java.util.List;

@Value
@Document
@Builder(toBuilder = true)
public class RawQuote {

  @Id
  private final String id;
  private final String type;

  @JsonProperty("user")
  private final String userId;
  private final List<String> hashtags;
  private final List<String> urls;
  private final String text;

  @JsonProperty("created_at")
  private final Date creationDate;
  @JsonProperty("recorded_at")
  private final Date recordingDate;

  private final List<String> mentions;
  @JsonProperty("mentioned_ids")
  private final List<String> mentionedIds;

  @JsonProperty("refers_to")
  private final String reference;
}
