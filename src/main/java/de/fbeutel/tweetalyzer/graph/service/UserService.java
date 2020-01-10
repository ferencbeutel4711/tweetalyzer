package de.fbeutel.tweetalyzer.graph.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import de.fbeutel.tweetalyzer.graph.domain.User;
import de.fbeutel.tweetalyzer.graph.domain.UserGraphRepository;

@Service
public class UserService {

  private final UserGraphRepository userGraphRepository;

  public UserService(final UserGraphRepository userGraphRepository) {
    this.userGraphRepository = userGraphRepository;
  }

  public List<User> findForGraph(final int limit, final String hashtag, final String username) {
    if (hashtag != null && username != null) {
      return userGraphRepository.findForGraph(limit, hashtagToSearchCriteria(hashtag), username);
    } else if (hashtag != null) {
      return userGraphRepository.findForGraphWithHashtag(limit, hashtagToSearchCriteria(hashtag));
    } else if (username != null) {
      return userGraphRepository.findForGraphWithUsername(limit, username);
    }

    return userGraphRepository.findForGraph(limit);
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

  private String hashtagToSearchCriteria(final String hashtag) {
    return "," + hashtag + ",";
  }
}
