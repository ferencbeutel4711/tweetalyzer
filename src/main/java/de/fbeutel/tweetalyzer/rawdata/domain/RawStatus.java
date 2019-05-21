package de.fbeutel.tweetalyzer.rawdata.domain;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Value
@Document
@Builder(toBuilder = true)
public class RawStatus {

  @Id
  private final String id;
  private final String type;
  private final String text;
}
