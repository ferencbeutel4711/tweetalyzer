package de.fbeutel.tweetalyzer.job.service;

import de.fbeutel.tweetalyzer.graph.job.ImportGraphRepliesJob;
import de.fbeutel.tweetalyzer.graph.job.ImportGraphRetweetsJob;
import de.fbeutel.tweetalyzer.graph.job.ImportGraphTweetsJob;
import de.fbeutel.tweetalyzer.graph.job.ImportGraphUsersJob;
import de.fbeutel.tweetalyzer.job.domain.JobStatus;
import de.fbeutel.tweetalyzer.job.exception.JobException;
import de.fbeutel.tweetalyzer.job.exception.JobInRunningMutexGroupException;
import de.fbeutel.tweetalyzer.job.exception.JobRunningException;
import de.fbeutel.tweetalyzer.job.domain.Job;
import de.fbeutel.tweetalyzer.job.domain.JobName;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import static de.fbeutel.tweetalyzer.job.domain.JobStatus.FAILED;
import static de.fbeutel.tweetalyzer.job.domain.JobStatus.RUNNING;

@Service
public class JobService {

  private final Map<JobName, Job> jobDefinitions;
  private final Map<JobName, Job> runningJobs = new ConcurrentHashMap<>();

  public JobService(ImportGraphRepliesJob importGraphRepliesJob, ImportGraphRetweetsJob importGraphRetweetsJob,
                    ImportGraphTweetsJob importGraphTweetsJob, ImportGraphUsersJob importGraphUsersJob) {

    this.jobDefinitions = new ConcurrentHashMap<>();
    this.jobDefinitions.put(importGraphRepliesJob.getJobName(), importGraphRepliesJob);
    this.jobDefinitions.put(importGraphRetweetsJob.getJobName(), importGraphRetweetsJob);
    this.jobDefinitions.put(importGraphTweetsJob.getJobName(), importGraphTweetsJob);
    this.jobDefinitions.put(importGraphUsersJob.getJobName(), importGraphUsersJob);
  }

  public void startJob(final Job job) throws JobException {
    if (runningJobs.containsKey(job.getJobName())) {
      throw new JobRunningException();
    }
    if (jobInRunningMutexGroup(job.getJobName())) {
      throw new JobInRunningMutexGroupException();
    }

    runningJobs.put(job.getJobName(), job);

    final Thread runnerThread = new Thread(() -> {
      job.setJobStatus(RUNNING);
      try {
        job.run();
      } catch (Throwable throwable) {
        job.setJobStatus(FAILED);
      }
      runningJobs.remove(job.getJobName());
    });
    runnerThread.start();
  }

  public Optional<Job> getJobInfo(final JobName jobName) {
    return Optional.ofNullable(runningJobs.get(jobName));
  }

  private boolean jobInRunningMutexGroup(final JobName jobName) {
    return runningJobs.entrySet().stream().anyMatch(jobInfo -> jobInfo.getValue().getJobMutexGroup().contains(jobName));
  }
}
