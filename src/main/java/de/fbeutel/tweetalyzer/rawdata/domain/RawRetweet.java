package de.fbeutel.tweetalyzer.rawdata.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Builder;
import lombok.Value;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;
import java.util.List;

@Value
@Document
@Builder(toBuilder = true)
public class RawRetweet {

  @Id
  private final String id;
  private final String type;

  @Indexed
  @JsonProperty("user")
  private final String userId;

  @JsonProperty("created_at")
  private final Date creationDate;
  @JsonProperty("recorded_at")
  private final Date recordingDate;

  @Indexed
  @JsonProperty("refers_to")
  private final String reference;
}
