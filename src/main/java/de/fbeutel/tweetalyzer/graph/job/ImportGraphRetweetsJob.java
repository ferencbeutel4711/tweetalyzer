package de.fbeutel.tweetalyzer.graph.job;

import de.fbeutel.tweetalyzer.job.domain.Job;
import de.fbeutel.tweetalyzer.job.domain.JobName;
import de.fbeutel.tweetalyzer.graph.service.TweetService;
import de.fbeutel.tweetalyzer.graph.service.UserService;
import de.fbeutel.tweetalyzer.rawdata.service.RawDataService;
import org.springframework.stereotype.Component;

import java.util.List;

import static de.fbeutel.tweetalyzer.job.domain.JobName.*;
import static de.fbeutel.tweetalyzer.job.domain.JobStatus.INITIAL;
import static java.util.Arrays.asList;

@Component
public class ImportGraphRetweetsJob extends Job {

  private static final String JOB_DESCRIPTION = "This job is importing all Retweets from the raw data into the graph database";
  private static final List<JobName> MUTEX_GROUP = asList(IMPORT_GRAPH_USERS_JOB, IMPORT_GRAPH_REPLIES_JOB,
          IMPORT_GRAPH_TWEETS_JOB);

  private final RawDataService rawDataService;
  private final UserService userService;
  private final TweetService tweetService;

  public ImportGraphRetweetsJob(RawDataService rawDataService, UserService userService, TweetService tweetService) {
    super(IMPORT_GRAPH_RETWEETS_JOB, JOB_DESCRIPTION, MUTEX_GROUP, INITIAL, 0, rawDataService.getRetweetSize(), 0);

    this.rawDataService = rawDataService;
    this.userService = userService;
    this.tweetService = tweetService;
  }

  @Override
  public void run() {
    rawDataService.getAllRetweets()
            .forEach(reTweet -> {
              try {
                userService.findByRawId(reTweet.getUserId())
                        .ifPresent(user -> tweetService.findByRawId(reTweet.getReference()).ifPresent(tweet -> {
                          if (!user.getReTweets().contains(tweet)) {
                            user.addReTweet(tweet);
                            userService.save(user);
                          }
                        }));
              } finally {
                this.reportCompletion(1);
              }
            });
  }
}
