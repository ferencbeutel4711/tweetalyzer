package de.fbeutel.tweetalyzer.rawdata.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Value;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@Value
@Document
@Builder(toBuilder = true)
public class RawUser {

  @Id
  private final String id;
  private final String type;

  @JsonProperty("screen_name")
  private final String screenName;

  @JsonProperty("created_at")
  private final Date creationDate;
  @JsonProperty("recorded_at")
  private final Date recordingDate;
}
