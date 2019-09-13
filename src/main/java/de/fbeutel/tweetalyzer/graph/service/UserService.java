package de.fbeutel.tweetalyzer.graph.service;

import de.fbeutel.tweetalyzer.graph.domain.Tweet;
import de.fbeutel.tweetalyzer.graph.domain.TweetGraphRepository;
import de.fbeutel.tweetalyzer.graph.domain.User;
import de.fbeutel.tweetalyzer.graph.domain.UserGraphRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {
  private final UserGraphRepository userGraphRepository;

  public UserService(final UserGraphRepository userGraphRepository) {
    this.userGraphRepository = userGraphRepository;
  }

  public long countUserNodes() {
    return userGraphRepository.count();
  }

  public long countTweetsRels() {
    return userGraphRepository.countTweetsRels();
  }

  public long countReTweetsRels() {
    return userGraphRepository.countReTweetsRels();
  }

  public Optional<User> findByRawId(final String rawId) {
    return userGraphRepository.findByRawId(rawId);
  }

  public User save(final User user) {
    return userGraphRepository.save(user);
  }
}
