package de.fbeutel.tweetalyzer.graph.domain;

import de.fbeutel.tweetalyzer.rawdata.domain.RawReply;
import de.fbeutel.tweetalyzer.rawdata.domain.RawStatus;
import lombok.*;
import org.neo4j.ogm.annotation.*;

import java.util.HashSet;
import java.util.Set;

import static org.neo4j.ogm.annotation.Relationship.UNDIRECTED;

@Data
@NodeEntity
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class Tweet {

  @Id
  @GeneratedValue
  private Long id;

  @Index(unique = true)
  private String rawId;
  @Index
  private String userId;
  private String text;
  private String replyTargetId;
  private Set<String> mentionedIds;

  @ToString.Exclude
  @EqualsAndHashCode.Exclude
  @Relationship(type = "replies_to", direction = UNDIRECTED)
  private Tweet target;

  @ToString.Exclude
  @EqualsAndHashCode.Exclude
  @Relationship(type = "mentions", direction = UNDIRECTED)
  private Set<User> mentionedUsers;

  public static Tweet fromRawData(final RawStatus rawStatus) {
    return Tweet.builder()
            .rawId(rawStatus.getId())
            .userId(rawStatus.getUserId())
            .text(rawStatus.getText())
            .mentionedIds(rawStatus.getMentionedIds())
            .mentionedUsers(new HashSet<>())
            .build();
  }

  public static Tweet fromRawData(final RawReply rawReply) {
    return Tweet.builder()
            .rawId(rawReply.getId())
            .userId(rawReply.getUserId())
            .text(rawReply.getText())
            .mentionedIds(rawReply.getMentionedIds())
            .mentionedUsers(new HashSet<>())
            .replyTargetId(rawReply.getReference())
            .build();
  }

  public void addMentionedUser(final User mentionedUser) {
    if (this.mentionedUsers == null) {
      this.mentionedUsers = new HashSet<>();
    }

    this.mentionedUsers.add(mentionedUser);
  }
}
