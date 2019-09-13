package de.fbeutel.tweetalyzer.job.domain;

import lombok.*;

import java.util.List;

import static lombok.AccessLevel.NONE;

@Value
@Builder
public class JobInformation {

  private final String id;
  private final String jobName;
  private final String readableJobName;
  private final String description;
  private final String jobStatus;
  private final double completion;
}
