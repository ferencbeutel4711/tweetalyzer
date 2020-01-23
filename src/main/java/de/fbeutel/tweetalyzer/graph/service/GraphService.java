package de.fbeutel.tweetalyzer.graph.service;

import static de.fbeutel.tweetalyzer.graph.domain.RelationshipType.*;
import static java.util.Collections.singletonList;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.stereotype.Service;

import de.fbeutel.tweetalyzer.graph.domain.PublicNetwork;
import de.fbeutel.tweetalyzer.graph.domain.PublicNodes;
import de.fbeutel.tweetalyzer.graph.domain.PublicRelationship;
import de.fbeutel.tweetalyzer.graph.domain.PublicRelationships;
import de.fbeutel.tweetalyzer.graph.domain.PublicTweet;
import de.fbeutel.tweetalyzer.graph.domain.PublicUser;
import de.fbeutel.tweetalyzer.graph.domain.Tweet;
import de.fbeutel.tweetalyzer.graph.domain.User;

@Service
public class GraphService {

    private static final Integer MAX_DEPTH = 20;

    private final UserService userService;

    public GraphService(final UserService userService) {
        this.userService = userService;
    }

    public PublicNetwork calculateNetwork(final int limit, final String hashtag, final String username) {
        final List<User> users = userService.findForGraph(limit, hashtag, username);

        return PublicNetwork.builder()
                .nodes(nodesForUsers(users, 0))
                .relationships(relationshipsForUsers(users, 0))
                .build();
    }

    private PublicRelationships relationshipsForUsers(final Collection<User> users, final int depth) {
        final int newDepth = depth + 1;

        if (depth > MAX_DEPTH || users == null) {
            return PublicRelationships.empty();
        }

        final Set<PublicRelationship> tweetsResults = new HashSet<>();
        final Set<PublicRelationship> reTweetsResults = new HashSet<>();
        final Set<PublicRelationship> mentionsResults = new HashSet<>();
        final Set<PublicRelationship> repliesResults = new HashSet<>();
        final Set<PublicRelationship> quotesResults = new HashSet<>();
        users.forEach(user -> {
            if (user.getCreatedTweets() != null) {
                user.getCreatedTweets()
                        .forEach(tweet -> tweetsResults.add(PublicRelationship.builder()
                                .type(TWEETS)
                                .source(user.getId().toString())
                                .target(tweet.getId().toString())
                                .build()));

            }

            if (user.getReTweets() != null) {
                user.getReTweets()
                        .forEach(tweet -> reTweetsResults.add(PublicRelationship.builder()
                                .type(RETWEETS)
                                .source(user.getId().toString())
                                .target(tweet.getId().toString())
                                .build()));
            }

            final PublicRelationships relsForCreatedTweets = relationshipsForTweets(user.getCreatedTweets(), newDepth);
            tweetsResults.addAll(relsForCreatedTweets.getTweets());
            reTweetsResults.addAll(relsForCreatedTweets.getReTweets());
            mentionsResults.addAll(relsForCreatedTweets.getMentions());
            repliesResults.addAll(relsForCreatedTweets.getReplies());
            quotesResults.addAll(relsForCreatedTweets.getQuotes());

            final PublicRelationships relsForReTweets = relationshipsForTweets(user.getReTweets(), newDepth);
            tweetsResults.addAll(relsForReTweets.getTweets());
            reTweetsResults.addAll(relsForReTweets.getReTweets());
            mentionsResults.addAll(relsForReTweets.getMentions());
            repliesResults.addAll(relsForReTweets.getReplies());
            quotesResults.addAll(relsForReTweets.getQuotes());
        });

        return PublicRelationships.builder()
                .tweets(tweetsResults)
                .reTweets(reTweetsResults)
                .mentions(mentionsResults)
                .replies(repliesResults)
                .quotes(quotesResults)
                .build();
    }

    private PublicRelationships relationshipsForTweets(final Collection<Tweet> tweets, final int depth) {
        final int newDepth = depth + 1;

        if (depth > MAX_DEPTH || tweets == null) {
            return PublicRelationships.empty();
        }

        final Set<PublicRelationship> tweetsResults = new HashSet<>();
        final Set<PublicRelationship> reTweetsResults = new HashSet<>();
        final Set<PublicRelationship> mentionsResults = new HashSet<>();
        final Set<PublicRelationship> repliesResults = new HashSet<>();
        final Set<PublicRelationship> quotesResults = new HashSet<>();
        tweets.forEach(tweet -> {
            if (tweet.getTarget() != null) {
                repliesResults.add(PublicRelationship.builder()
                        .type(REPLIES_TO)
                        .source(tweet.getId().toString())
                        .target(tweet.getTarget().getId().toString())
                        .build());

                final PublicRelationships relsForTarget = relationshipsForTweets(singletonList(tweet.getTarget()), newDepth);
                tweetsResults.addAll(relsForTarget.getTweets());
                reTweetsResults.addAll(relsForTarget.getReTweets());
                mentionsResults.addAll(relsForTarget.getMentions());
                repliesResults.addAll(relsForTarget.getReplies());
            }
            if (tweet.getQuoteTarget() != null) {
                quotesResults.add(PublicRelationship.builder()
                        .type(QUOTES)
                        .source(tweet.getId().toString())
                        .target(tweet.getQuoteTarget().getId().toString())
                        .build());

                final PublicRelationships relsForTarget = relationshipsForTweets(singletonList(tweet.getQuoteTarget()), newDepth);
                tweetsResults.addAll(relsForTarget.getTweets());
                reTweetsResults.addAll(relsForTarget.getReTweets());
                mentionsResults.addAll(relsForTarget.getMentions());
                repliesResults.addAll(relsForTarget.getReplies());
                quotesResults.addAll(relsForTarget.getQuotes());
            }

            if (tweet.getMentionedUsers() != null) {
                tweet.getMentionedUsers().forEach(mentionedUser -> mentionsResults.add(PublicRelationship.builder()
                        .type(MENTIONS)
                        .source(tweet.getId().toString())
                        .target(mentionedUser.getId().toString())
                        .build()));
            }

            final PublicRelationships relsForMentions = relationshipsForUsers(tweet.getMentionedUsers(), newDepth);
            tweetsResults.addAll(relsForMentions.getTweets());
            reTweetsResults.addAll(relsForMentions.getReTweets());
            mentionsResults.addAll(relsForMentions.getMentions());
            repliesResults.addAll(relsForMentions.getReplies());
            quotesResults.addAll(relsForMentions.getQuotes());
        });

        return PublicRelationships.builder()
                .tweets(tweetsResults)
                .reTweets(reTweetsResults)
                .mentions(mentionsResults)
                .replies(repliesResults)
                .quotes(quotesResults)
                .build();
    }

    private PublicNodes nodesForTweets(final Collection<Tweet> tweets, final int depth) {
        final int newDepth = depth + 1;
        if (depth > MAX_DEPTH || tweets == null) {
            return PublicNodes.empty();
        }

        final Set<PublicUser> resultUsers = new HashSet<>();
        final Set<PublicTweet> resultTweets = new HashSet<>();
        tweets.forEach(tweet -> {
            resultTweets.add(tweet.toPublicTweet());

            if (tweet.getTarget() != null) {
                final PublicNodes targetNodes = nodesForTweets(singletonList(tweet.getTarget()), newDepth);
                resultUsers.addAll(targetNodes.getUsers());
                resultTweets.addAll(targetNodes.getTweets());
            }

            if (tweet.getQuoteTarget() != null) {
                final PublicNodes targetNodes = nodesForTweets(singletonList(tweet.getQuoteTarget()), newDepth);
                resultUsers.addAll(targetNodes.getUsers());
                resultTweets.addAll(targetNodes.getTweets());
            }

            final PublicNodes nodesOfMentions = nodesForUsers(tweet.getMentionedUsers(), newDepth);
            resultUsers.addAll(nodesOfMentions.getUsers());
            resultTweets.addAll(nodesOfMentions.getTweets());
        });

        return PublicNodes.builder()
                .users(resultUsers)
                .tweets(resultTweets)
                .build();
    }

    private PublicNodes nodesForUsers(final Collection<User> users, final int depth) {
        final int newDepth = depth + 1;

        if (depth > MAX_DEPTH || users == null) {
            return PublicNodes.empty();
        }

        final Set<PublicUser> resultUsers = new HashSet<>();
        final Set<PublicTweet> resultTweets = new HashSet<>();
        users.forEach(user -> {
            final PublicUser currentPublicUser = user.toPublicUser();
            resultUsers.add(currentPublicUser);

            final PublicNodes nodesOfCreatedTweets = nodesForTweets(user.getCreatedTweets(), newDepth);
            resultUsers.addAll(nodesOfCreatedTweets.getUsers());
            resultTweets.addAll(nodesOfCreatedTweets.getTweets());

            final PublicNodes nodesOfReTweets = nodesForTweets(user.getReTweets(), newDepth);
            resultUsers.addAll(nodesOfReTweets.getUsers());
            resultTweets.addAll(nodesOfReTweets.getTweets());
        });

        return PublicNodes.builder()
                .users(resultUsers)
                .tweets(resultTweets)
                .build();
    }
}
