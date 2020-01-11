package de.fbeutel.tweetalyzer.job.service;

import de.fbeutel.tweetalyzer.common.exception.NotFoundException;
import de.fbeutel.tweetalyzer.graph.job.*;
import de.fbeutel.tweetalyzer.job.exception.JobException;
import de.fbeutel.tweetalyzer.job.exception.JobInRunningMutexGroupException;
import de.fbeutel.tweetalyzer.job.exception.JobRunningException;
import de.fbeutel.tweetalyzer.job.domain.Job;
import de.fbeutel.tweetalyzer.job.domain.JobName;
import de.fbeutel.tweetalyzer.rawdata.job.RawDataImportJob;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import static de.fbeutel.tweetalyzer.job.domain.JobStatus.*;

@Slf4j
@Service
public class JobService {

  private final Map<JobName, Job> jobDefinitions;
  private final Map<JobName, Job> runningJobs = new ConcurrentHashMap<>();

  public JobService(final ImportGraphRepliesJob importGraphRepliesJob, final ImportGraphRetweetsJob importGraphRetweetsJob,
                    final ImportGraphTweetsJob importGraphTweetsJob, final ImportGraphUsersJob importGraphUsersJob,
                    final ImportGraphQuotesJob importGraphQuotesJob, final RawDataImportJob rawDataImportJob) {

    this.jobDefinitions = new ConcurrentHashMap<>();
    this.jobDefinitions.put(importGraphRepliesJob.getJobName(), importGraphRepliesJob);
    this.jobDefinitions.put(importGraphRetweetsJob.getJobName(), importGraphRetweetsJob);
    this.jobDefinitions.put(importGraphTweetsJob.getJobName(), importGraphTweetsJob);
    this.jobDefinitions.put(importGraphUsersJob.getJobName(), importGraphUsersJob);
    this.jobDefinitions.put(importGraphQuotesJob.getJobName(), importGraphQuotesJob);
    this.jobDefinitions.put(rawDataImportJob.getJobName(), rawDataImportJob);
  }

  /**
   * Start the execution of the given job if possible. If the Job is already running or if it is forbidden to run the job next
   * to another job that is already running it will fail.
   *
   * @param jobName The Job Name enum value
   * @throws JobRunningException             when this job is already running
   * @throws JobInRunningMutexGroupException when another job is running which must be running mutually exclusive to this
   */
  public void startJob(final JobName jobName) throws JobException {
    final Job job = this.jobDefinitions.get(jobName);
    if (job == null) {
      log.error("Job with the name: " + jobName.getReadableName() + " is not found as a job definition");
      throw new NotFoundException();
    }

    if (runningJobs.containsKey(job.getJobName())) {
      throw new JobRunningException();
    }
    if (jobInRunningMutexGroup(job)) {
      throw new JobInRunningMutexGroupException();
    }

    runningJobs.put(job.getJobName(), job);

    final Thread runnerThread = new Thread(() -> {
      job.setJobStatus(RUNNING);
      try {
        job.run();
        job.setJobStatus(FINISHED);
      } catch (final Throwable throwable) {
        log.error("error during job execution", throwable);
        job.setJobStatus(FAILED);
      }
      runningJobs.remove(job.getJobName());
    });
    runnerThread.start();
  }

  /**
   * Look in running jobs first; if not found, try to return at least the job definition
   *
   * @param jobName The Job Name enum value
   * @return Optional containing the requested Job if present
   */
  public Optional<Job> getJobInfo(final JobName jobName) {
    return Optional.ofNullable(Optional.ofNullable(runningJobs.get(jobName)).orElse(jobDefinitions.get(jobName)));
  }

  private boolean jobInRunningMutexGroup(final Job job) {
      boolean runningJobInJobsMutexGroup = runningJobs.entrySet().stream()
              .anyMatch(jobInfo -> jobInfo.getValue().getMutexGroup().contains(job.getJobName()));
      boolean jobInRunningJobsMutexGroup = job.getMutexGroup().stream()
              .anyMatch(runningJobs::containsKey);

      return runningJobInJobsMutexGroup || jobInRunningJobsMutexGroup;
  }
}
