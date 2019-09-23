package de.fbeutel.tweetalyzer.graph.job;

import de.fbeutel.tweetalyzer.job.domain.Job;
import de.fbeutel.tweetalyzer.job.domain.JobName;
import de.fbeutel.tweetalyzer.graph.domain.Tweet;
import de.fbeutel.tweetalyzer.graph.domain.User;
import de.fbeutel.tweetalyzer.graph.service.TweetService;
import de.fbeutel.tweetalyzer.graph.service.UserService;
import de.fbeutel.tweetalyzer.rawdata.service.RawDataService;
import org.springframework.stereotype.Component;

import java.util.List;

import static de.fbeutel.tweetalyzer.job.domain.JobName.*;
import static de.fbeutel.tweetalyzer.job.domain.JobStatus.INITIAL;
import static java.util.Arrays.asList;


/**
 * Will import all Users as nodes.
 * Will update the tweets relation:
 * User->[tweets]->Tweet
 * Will update the mentions relation:
 * Tweet->[mentions]->User
 * Will update the reTweets relation:
 * User->[reTweets]->Tweet
 * This operation is NOT idempotent as it only processes not yet imported rawUsers
 */
@Component
public class ImportGraphUsersJob extends Job {

  private static final String JOB_DESCRIPTION = "This job is importing all Users from the raw data into the graph database";
  private static final List<JobName> MUTEX_GROUP = asList(IMPORT_GRAPH_REPLIES_JOB, IMPORT_GRAPH_RETWEETS_JOB,
          IMPORT_GRAPH_TWEETS_JOB);

  private final RawDataService rawDataService;
  private final UserService userService;
  private final TweetService tweetService;

  public ImportGraphUsersJob(RawDataService rawDataService, UserService userService, TweetService tweetService) {
    super(IMPORT_GRAPH_USERS_JOB, JOB_DESCRIPTION, MUTEX_GROUP, rawDataService::getUsersSize, INITIAL, 0, 0, 0);

    this.rawDataService = rawDataService;
    this.userService = userService;
    this.tweetService = tweetService;
  }

  @Override
  protected void execute() {
    rawDataService.getAllUsers()
            .map(User::fromRawData)
            .forEach(user -> {
              try {
                if (userService.findByRawId(user.getRawId()).isEmpty()) {
                  final List<Tweet> allTweetsByUser = tweetService.findAllTweetsByUserId(user.getRawId());
                  allTweetsByUser.forEach(user::addTweet);

                  // find tweets in graph by reference of rawReTweet (which is the tweets raw id)
                  rawDataService.getAllRetweetsByUserId(user.getRawId())
                          .forEach(reTweet -> tweetService.findByRawId(reTweet.getReference()).ifPresent(user::addReTweet));

                  userService.save(user);

                  tweetService.findAllByMentionedIdsContaining(user.getRawId()).forEach(tweet -> {
                    tweet.addMentionedUser(user);
                    tweetService.save(tweet);
                  });
                }

              } finally {
                this.reportCompletion(1);
              }
            });
  }
}
