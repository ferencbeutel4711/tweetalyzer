package de.fbeutel.tweetalyzer.rawdata.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Builder;
import lombok.Value;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Value
@Document
@Builder(toBuilder = true)
public class RawRetweet {

  @Id
  private final String id;
  private final String type;
  @JsonProperty("mentioned_ids")
  private final List<String> mentionedIds;
}
