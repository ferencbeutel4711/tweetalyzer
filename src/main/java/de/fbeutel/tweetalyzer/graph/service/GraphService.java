package de.fbeutel.tweetalyzer.graph.service;

import de.fbeutel.tweetalyzer.graph.domain.TweetGraphRepository;
import de.fbeutel.tweetalyzer.graph.domain.UserGraphRepository;
import org.springframework.stereotype.Service;

@Service
public class GraphService {
  private final TweetGraphRepository tweetGraphRepository;
  private final UserGraphRepository userGraphRepository;

  public GraphService(TweetGraphRepository tweetGraphRepository, UserGraphRepository userGraphRepository) {
    this.tweetGraphRepository = tweetGraphRepository;
    this.userGraphRepository = userGraphRepository;
  }

  public long countUserNodes() {
    return userGraphRepository.count();
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

  public long countTweetsRels() {
    return userGraphRepository.countTweetsRels();
  }

  public long countReTweetsRels() {
    return userGraphRepository.countReTweetsRels();
  }
}
