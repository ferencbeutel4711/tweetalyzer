package de.fbeutel.tweetalyzer.graph.domain;

import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface UserGraphRepository extends CrudRepository<User, Long> {

  Optional<User> findByRawId(final String rawId);
}
