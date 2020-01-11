package de.fbeutel.tweetalyzer.graph.job;

import de.fbeutel.tweetalyzer.graph.domain.Tweet;
import de.fbeutel.tweetalyzer.graph.service.TweetService;
import de.fbeutel.tweetalyzer.graph.service.UserService;
import de.fbeutel.tweetalyzer.job.domain.Job;
import de.fbeutel.tweetalyzer.job.domain.JobName;
import de.fbeutel.tweetalyzer.rawdata.service.RawDataService;
import org.springframework.stereotype.Component;

import java.util.List;

import static de.fbeutel.tweetalyzer.job.domain.JobName.*;
import static de.fbeutel.tweetalyzer.job.domain.JobStatus.INITIAL;
import static java.util.Arrays.asList;

/**
 * Will import all Quotes as Tweet nodes.
 * Will update the quotes relation:
 * User->[tweets]->Tweet
 * Will update the mentions relation:
 * Tweet->[mentions]->User
 * Will update the reTweets relation:
 * User->[reTweets]->Tweet
 * Will update the quotes relation:
 * Tweet->[quotes]->Tweet
 * This operation is NOT idempotent as it only processes not yet imported rawQuotes
 */
@Component
public class ImportGraphQuotesJob extends Job {

    private static final String JOB_DESCRIPTION = "This job is importing all Quotes from the raw data into the graph database";
    private static final List<JobName> MUTEX_GROUP = asList(IMPORT_GRAPH_USERS_JOB, IMPORT_GRAPH_RETWEETS_JOB,
            IMPORT_GRAPH_TWEETS_JOB, IMPORT_GRAPH_REPLIES_JOB);

    private final RawDataService rawDataService;
    private final UserService userService;
    private final TweetService tweetService;

    public ImportGraphQuotesJob(RawDataService rawDataService, UserService userService, TweetService tweetService) {
        super(IMPORT_GRAPH_QUOTES_JOB, JOB_DESCRIPTION, MUTEX_GROUP, rawDataService::getQuotesSize, INITIAL, 0, 0, 0);

        this.rawDataService = rawDataService;
        this.userService = userService;
        this.tweetService = tweetService;
    }

    @Override
    protected void execute() {
        rawDataService.getAllQuotes()
                .map(Tweet::fromRawData)
                .forEach(tweet -> {
                    try {
                        if (tweetService.findByRawId(tweet.getRawId()).isEmpty()) {
                            tweet.getMentionedIds().forEach(userId -> userService.findByRawId(userId).ifPresent(tweet::addMentionedUser));

                            tweetService.save(tweet);

                            userService.findByRawId(tweet.getUserId()).ifPresent(authorOfThisTweet -> {
                                authorOfThisTweet.addTweet(tweet);
                                userService.save(authorOfThisTweet);
                            });

                            tweetService.findByRawId(tweet.getQuoteTargetId()).ifPresent(tweet::setQuoteTarget);

                            rawDataService.getAllRetweetsByReference(tweet.getRawId())
                                    .forEach(reTweet -> userService.findByRawId(reTweet.getUserId()).ifPresent(user -> {
                                        user.addReTweet(tweet);
                                        userService.save(user);
                                    }));
                        }

                    } finally {
                        this.reportCompletion(1);
                    }
                });
    }
}
