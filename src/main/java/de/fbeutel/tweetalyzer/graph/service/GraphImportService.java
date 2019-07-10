package de.fbeutel.tweetalyzer.graph.service;

import de.fbeutel.tweetalyzer.common.domain.ImportRunningException;
import de.fbeutel.tweetalyzer.common.util.PerformanceGauge;
import de.fbeutel.tweetalyzer.graph.domain.Tweet;
import de.fbeutel.tweetalyzer.graph.domain.TweetGraphRepository;
import de.fbeutel.tweetalyzer.graph.domain.User;
import de.fbeutel.tweetalyzer.graph.domain.UserGraphRepository;
import de.fbeutel.tweetalyzer.rawdata.service.RawDataService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * This class is responsible for importing the Raw Data into a Graph Database.
 * It assumes the following structure:
 *
 * ---------                ---------            --------
 * | Tweet | - replies_to - | Tweet | - tweets - | User |
 * ---------                ---------            --------
 *          \                   |
 *           \               mentions
 *            \                 |
 *             \             --------
 *              reTweets --- | User |
 *                           --------
 *
 * Each User can tweet n tweets.
 * Each User can reTweet n tweets.
 * Each Tweet can mention n Users.
 * Each Tweet can be a reply to another tweet.
 */
@Slf4j
@Service
public class GraphImportService {

  private final UserGraphRepository userRepository;
  private final TweetGraphRepository tweetRepository;
  private final RawDataService rawDataService;

  private final AtomicBoolean importRunning = new AtomicBoolean(false);

  public GraphImportService(UserGraphRepository userRepository, TweetGraphRepository tweetRepository,
                            RawDataService rawDataService) {
    this.userRepository = userRepository;
    this.tweetRepository = tweetRepository;
    this.rawDataService = rawDataService;
  }

  /**
   * Will import all Users as nodes.
   * Will update the tweets relation:
   * (User)->[tweets]->Tweet
   * Will update the mentions relation:
   * (Tweet)->[mentions]->User
   * Will update the reTweets relation:
   * (User)->[reTweets]->Tweet
   * This operation is NOT idempotent as it only processes not yet imported rawUsers
   *
   * @throws ImportRunningException when another import is running
   */
  public void startUserImport() throws ImportRunningException {
    if (importRunning.get()) {
      log.error("import is already running!");
      throw new ImportRunningException();
    }

    importRunning.set(true);

    new Thread(() -> {
      try {
        final PerformanceGauge usersGauge = new PerformanceGauge("GraphImportService_users", rawDataService.getUsersSize(), 1.0);

        usersGauge.start();

        rawDataService.getAllUsers()
                .map(User::fromRawData)
                .forEach(user -> {
                  if (userRepository.findByRawId(user.getRawId()).isEmpty()) {
                    final List<Tweet> allTweetsByUser = tweetRepository.findAllByUserId(user.getRawId());
                    allTweetsByUser.forEach(user::addTweet);

                    // find tweets in graph by reference of rawReTweet (which is the tweets raw id)
                    rawDataService.getAllRetweetsByUserId(user.getRawId())
                            .forEach(reTweet -> tweetRepository.findByRawId(reTweet.getReference()).ifPresent(user::addReTweet));

                    userRepository.save(user);

                    tweetRepository.findAllByMentionedIdsContaining(user.getRawId()).forEach(tweet -> {
                      tweet.addMentionedUser(user);
                      tweetRepository.save(tweet);
                    });
                  }

                  usersGauge.reportCompletion(1);
                });
      } finally {
        importRunning.set(false);
      }
    }).start();
  }

  /**
   * Will import all Tweets as nodes.
   * Will update the tweets relation:
   * (User)->[tweets]->Tweet
   * Will update the mentions relation:
   * (Tweet)->[mentions]->User
   * Will update the reTweets relation:
   * (User)->[reTweets]->Tweet
   * Will update the replies_to relation:
   * (Tweet)->[replies_to]->Tweet
   * This operation is NOT idempotent as it only processes not yet imported rawTweets
   *
   * @throws ImportRunningException when another import is running
   */
  public void startTweetImport() throws ImportRunningException {
    if (importRunning.get()) {
      log.error("import is already running!");
      throw new ImportRunningException();
    }

    importRunning.set(true);

    new Thread(() -> {
      try {
        final PerformanceGauge tweetsGauge = new PerformanceGauge("GraphImportService_tweets", rawDataService.getStatusSize(),
                1.0);

        tweetsGauge.start();

        rawDataService.getAllStatus()
                .map(Tweet::fromRawData)
                .forEach(tweet -> {
                  if (tweet.getRawId() == null || tweetRepository.findByRawId(tweet.getRawId()).isEmpty()) {
                    tweet.getMentionedIds().forEach(userId -> userRepository.findByRawId(userId).ifPresent(tweet::addMentionedUser));

                    tweetRepository.save(tweet);

                    userRepository.findByRawId(tweet.getUserId()).ifPresent(authorOfThisTweet -> {
                      authorOfThisTweet.addTweet(tweet);
                      userRepository.save(authorOfThisTweet);
                    });

                    tweetRepository.findByReplyTargetId(tweet.getRawId()).ifPresent(reply -> {
                      reply.setTarget(tweet);
                      tweetRepository.save(reply);
                    });

                    rawDataService.getAllRetweetsByReference(tweet.getRawId())
                            .forEach(reTweet -> userRepository.findByRawId(reTweet.getUserId()).ifPresent(user -> {
                              user.addReTweet(tweet);
                              userRepository.save(user);
                            }));
                  }

                  tweetsGauge.reportCompletion(1);
                });
      } finally {
        importRunning.set(false);
      }
    }).start();
  }

  /**
   * Will update the reTweets relation:
   * (User)->[reTweets]->Tweet
   * This operation is idempotent
   *
   * @throws ImportRunningException when another import is running
   */
  public void startReTweetImport() throws ImportRunningException {
    if (importRunning.get()) {
      log.error("import is already running!");
      throw new ImportRunningException();
    }

    importRunning.set(true);

    new Thread(() -> {
      try {
        final PerformanceGauge reTweetsGauge = new PerformanceGauge("GraphImportService_reTweets",
                rawDataService.getRetweetSize(), 1.0);

        reTweetsGauge.start();

        rawDataService.getAllRetweets()
                .forEach(reTweet -> {
                  userRepository.findByRawId(reTweet.getUserId())
                          .ifPresent(user -> tweetRepository.findByRawId(reTweet.getReference()).ifPresent(tweet -> {
                            if (!user.getReTweets().contains(tweet)) {
                              user.addReTweet(tweet);
                              userRepository.save(user);
                            }
                          }));

                  reTweetsGauge.reportCompletion(1);
                });
      } finally {
        importRunning.set(false);
      }
    }).start();
  }

  /**
   * Will import all Replies as Tweet nodes.
   * Will update the tweets relation:
   * (User)->[tweets]->Tweet
   * Will update the replies_to relation:
   * (Tweet)->[replies_to]->Tweet
   * This operation is NOT idempotent as it only processes not yet imported rawReplies
   *
   * @throws ImportRunningException when another import is running
   */
  public void startReplyImport() throws ImportRunningException {
    if (importRunning.get()) {
      log.error("import is already running!");
      throw new ImportRunningException();
    }

    importRunning.set(true);

    new Thread(() -> {
      try {
        final PerformanceGauge repliesGauge = new PerformanceGauge("GraphImportService_replies", rawDataService.getReplySize(),
                1.0);

        repliesGauge.start();

        rawDataService.getAllReplies()
                .map(Tweet::fromRawData)
                .forEach(tweet -> {
                  if (tweet.getRawId() == null || tweetRepository.findByRawId(tweet.getRawId()).isEmpty()) {
                    tweet.getMentionedIds().forEach(userId -> userRepository.findByRawId(userId).ifPresent(tweet::addMentionedUser));
                    tweetRepository.findByRawId(tweet.getReplyTargetId()).ifPresent(tweet::setTarget);

                    tweetRepository.save(tweet);

                    userRepository.findByRawId(tweet.getUserId()).ifPresent(authorOfThisTweet -> {
                      authorOfThisTweet.addTweet(tweet);
                      userRepository.save(authorOfThisTweet);
                    });

                    rawDataService.getAllRetweetsByReference(tweet.getRawId())
                            .forEach(reTweet -> userRepository.findByRawId(reTweet.getUserId()).ifPresent(user -> {
                              user.addReTweet(tweet);
                              userRepository.save(user);
                            }));
                  }

                  repliesGauge.reportCompletion(1);
                });
      } finally {
        importRunning.set(false);
      }
    }).start();
  }
}
