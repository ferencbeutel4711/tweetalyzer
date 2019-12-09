package de.fbeutel.tweetalyzer.job.domain;

import lombok.*;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Supplier;

import static lombok.AccessLevel.NONE;

@Data
@Slf4j
@AllArgsConstructor
public abstract class Job implements Runnable {

  private final JobName jobName;
  private final String description;

  private final List<JobName> mutexGroup;

  private final Supplier<Long> amountOfTotalWorkSupplier;

  private JobStatus jobStatus;
  private double completion;
  private long amountOfTotalWork;

  @Getter(NONE)
  private double amountOfWorkDone;

  protected abstract void execute() throws Exception;

  @Override
  public void run() {
    this.amountOfWorkDone = 0;
    this.amountOfTotalWork = this.amountOfTotalWorkSupplier.get();
    final long beginning = System.currentTimeMillis();

    try {
      this.execute();
    } catch (final Exception exception) {
      throw new RuntimeException(exception);
    }

    final long end = System.currentTimeMillis();
    log.info("job completed! it took {}s", (end - beginning) / 1000);
  }

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
