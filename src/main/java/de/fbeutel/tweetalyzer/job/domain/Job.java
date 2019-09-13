package de.fbeutel.tweetalyzer.job.domain;

import lombok.*;

import java.util.List;

import static lombok.AccessLevel.NONE;

@Data
@AllArgsConstructor
public abstract class Job implements Runnable {

  private final JobName jobName;
  private final String description;

  private final List<JobName> jobMutexGroup;

  private JobStatus jobStatus;
  private double completion;
  private double amountOfTotalWork;

  @Getter(NONE)
  private double amountOfWorkDone;

  public JobInformation toJobInformation() {
    return JobInformation.builder()
            .jobName(this.jobName.name())
            .readableJobName(this.jobName.getReadableName())
            .description(this.description)
            .jobStatus(this.jobStatus.getReadableName())
            .completion(this.completion)
            .build();
  }

  protected void reportCompletion(final int amountOfCompletedWork) {
    this.amountOfWorkDone += amountOfCompletedWork;
    this.completion = this.amountOfWorkDone / this.amountOfTotalWork;
  }
}
