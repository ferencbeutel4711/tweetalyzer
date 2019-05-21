package de.fbeutel.tweetalyzer.rawdata.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Value;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Value
@Document
@Builder(toBuilder = true)
public class RawReply {

  @Id
  private final String id;
  private final String type;
  private final String text;
  @JsonProperty("refers_to")
  private final String refersTo;
}
