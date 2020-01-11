package de.fbeutel.tweetalyzer.graph.service;

import de.fbeutel.tweetalyzer.graph.domain.Tweet;
import de.fbeutel.tweetalyzer.graph.domain.TweetGraphRepository;
import de.fbeutel.tweetalyzer.graph.domain.User;
import de.fbeutel.tweetalyzer.graph.domain.UserGraphRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class TweetService {
    private final TweetGraphRepository tweetGraphRepository;

    public TweetService(final TweetGraphRepository tweetGraphRepository) {
        this.tweetGraphRepository = tweetGraphRepository;
    }

    public long countTweetNodes() {
        return tweetGraphRepository.count();
    }

    public long countRepliesToRels() {
        return tweetGraphRepository.countRepliesToRels();
    }

    public long countMentionsRels() {
        return tweetGraphRepository.countMentionsRels();
    }

    public long countQuoteRels() {
        return tweetGraphRepository.countQuoteRels();
    }

    public Tweet save(final Tweet tweet) {
        return tweetGraphRepository.save(tweet);
    }

    public Optional<Tweet> findByRawId(final String rawId) {
        return tweetGraphRepository.findByRawId(rawId);
    }

    public Optional<Tweet> findByReplyTargetId(final String replyTargetId) {
        return tweetGraphRepository.findByReplyTargetId(replyTargetId);
    }

    public Optional<Tweet> findByQuoteTargetId(final String quoteTargetId) {
        return tweetGraphRepository.findByQuoteTargetId(quoteTargetId);
    }

    public List<Tweet> findAllTweetsByUserId(final String userId) {
        return tweetGraphRepository.findAllByUserId(userId);
    }

    public List<Tweet> findAllByMentionedIdsContaining(String userId) {
        return tweetGraphRepository.findAllByMentionedIdsContaining(userId);
    }
}
