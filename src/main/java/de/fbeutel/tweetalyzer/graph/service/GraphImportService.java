package de.fbeutel.tweetalyzer.graph.service;

import de.fbeutel.tweetalyzer.job.domain.Job;
import de.fbeutel.tweetalyzer.job.exception.JobException;
import de.fbeutel.tweetalyzer.job.exception.JobInRunningMutexGroupException;
import de.fbeutel.tweetalyzer.job.exception.JobRunningException;
import de.fbeutel.tweetalyzer.job.service.JobService;
import de.fbeutel.tweetalyzer.graph.job.ImportGraphRepliesJob;
import de.fbeutel.tweetalyzer.graph.job.ImportGraphRetweetsJob;
import de.fbeutel.tweetalyzer.graph.job.ImportGraphTweetsJob;
import de.fbeutel.tweetalyzer.graph.job.ImportGraphUsersJob;
import de.fbeutel.tweetalyzer.rawdata.service.RawDataService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * This class is responsible for importing the Raw Data into a Graph Database.
 * It assumes the following structure:
 * <p>
 * ---------                ---------            --------
 * | Tweet | - replies_to - | Tweet | - tweets - | User |
 * ---------                ---------            --------
 * \                   |
 * \               mentions
 * \                 |
 * \             --------
 * reTweets --- | User |
 * --------
 * <p>
 * Each User can tweet n tweets.
 * Each User can reTweet n tweets.
 * Each Tweet can mention n Users.
 * Each Tweet can be a reply to another tweet.
 */
@Slf4j
@Service
public class GraphImportService {

  private final RawDataService rawDataService;
  private final UserService userService;
  private final TweetService tweetService;
  private final JobService jobService;

  public GraphImportService(RawDataService rawDataService, UserService userService, TweetService tweetService,
                            JobService jobService) {
    this.rawDataService = rawDataService;
    this.userService = userService;
    this.tweetService = tweetService;
    this.jobService = jobService;
  }

  /**
   * Will import all Users as nodes.
   * Will update the tweets relation:
   * User->[tweets]->Tweet
   * Will update the mentions relation:
   * Tweet->[mentions]->User
   * Will update the reTweets relation:
   * User->[reTweets]->Tweet
   * This operation is NOT idempotent as it only processes not yet imported rawUsers
   *
   * @throws JobRunningException             when this job is already running
   * @throws JobInRunningMutexGroupException when another job is running which should be running mutually exclusive to this
   */
  public Job startUserImport() {
    final ImportGraphUsersJob graphUsersJob = new ImportGraphUsersJob(rawDataService, userService, tweetService);
    jobService.startJob(graphUsersJob);

    return graphUsersJob;
  }

  /**
   * Will import all Tweets as nodes.
   * Will update the tweets relation:
   * User->[tweets]->Tweet
   * Will update the mentions relation:
   * Tweet->[mentions]->User
   * Will update the reTweets relation:
   * User->[reTweets]->Tweet
   * Will update the replies_to relation:
   * Tweet->[replies_to]->Tweet
   * This operation is NOT idempotent as it only processes not yet imported rawTweets
   *
   * @throws JobRunningException             when this job is already running
   * @throws JobInRunningMutexGroupException when another job is running which should be running mutually exclusive to this
   */
  public ImportGraphTweetsJob startTweetImport() {
    final ImportGraphTweetsJob graphTweetsJob = new ImportGraphTweetsJob(rawDataService, userService, tweetService);
    jobService.startJob(graphTweetsJob);

    return graphTweetsJob;
  }

  /**
   * Will import all Replies as Tweet nodes.
   * Will update the tweets relation:
   * User->[tweets]->Tweet
   * Will update the replies_to relation:
   * Tweet->[replies_to]->Tweet
   * This operation is NOT idempotent as it only processes not yet imported rawReplies
   *
   * @throws JobRunningException             when this job is already running
   * @throws JobInRunningMutexGroupException when another job is running which should be running mutually exclusive to this
   */
  public ImportGraphRepliesJob startReplyImport() {
    final ImportGraphRepliesJob graphRepliesJob = new ImportGraphRepliesJob(rawDataService, userService, tweetService);
    jobService.startJob(graphRepliesJob);

    return graphRepliesJob;
  }

  /**
   * Will update the reTweets relation:
   * User->[reTweets]->Tweet
   * This operation is idempotent
   *
   * @throws JobRunningException             when this job is already running
   * @throws JobInRunningMutexGroupException when another job is running which should be running mutually exclusive to this
   */
  public ImportGraphRetweetsJob startReTweetImport() {
    final ImportGraphRetweetsJob importGraphRetweetsJob = new ImportGraphRetweetsJob(rawDataService, userService, tweetService);
    jobService.startJob(importGraphRetweetsJob);

    return importGraphRetweetsJob;
  }
}
