package de.fbeutel.tweetalyzer.job.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum JobStatus {
  INITIAL("initial"), RUNNING("running"), FINISHED("finished"), FAILED("failed");

  private final String readableName;
}
