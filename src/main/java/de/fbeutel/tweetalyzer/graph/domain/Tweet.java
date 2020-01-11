package de.fbeutel.tweetalyzer.graph.domain;

import com.fasterxml.jackson.annotation.JsonBackReference;
import de.fbeutel.tweetalyzer.rawdata.domain.RawQuote;
import de.fbeutel.tweetalyzer.rawdata.domain.RawReply;
import de.fbeutel.tweetalyzer.rawdata.domain.RawStatus;
import lombok.*;
import org.neo4j.ogm.annotation.*;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static de.fbeutel.tweetalyzer.graph.domain.NodeType.TWEET;
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
  private String quoteTargetId;
  private Set<String> mentionedIds;
  private List<String> hashTags;

  @Index
  private String hashTagSearchField;

  @ToString.Exclude
  @EqualsAndHashCode.Exclude
  @Relationship(type = "replies_to", direction = UNDIRECTED)
  @JsonBackReference
  private Tweet target;

  @JsonBackReference
  @ToString.Exclude
  @EqualsAndHashCode.Exclude
  @Relationship(type = "mentions", direction = UNDIRECTED)
  private Set<User> mentionedUsers;

  @ToString.Exclude
  @EqualsAndHashCode.Exclude
  @Relationship(type = "quotes", direction = UNDIRECTED)
  @JsonBackReference
  private Tweet quoteTarget;

  public static Tweet fromRawData(final RawStatus rawStatus) {
    return Tweet.builder()
            .rawId(rawStatus.getId())
            .userId(rawStatus.getUserId())
            .text(rawStatus.getText())
            .mentionedIds(rawStatus.getMentionedIds())
            .mentionedUsers(new HashSet<>())
            .hashTags(rawStatus.getHashtags())
            .hashTagSearchField("," + String.join(",", rawStatus.getHashtags()) + ",")
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
            .hashTags(rawReply.getHashtags())
            .hashTagSearchField("," + String.join(",", rawReply.getHashtags()) + ",")
            .build();
  }

  public static Tweet fromRawData(final RawQuote rawQuote) {
    return Tweet.builder()
            .rawId(rawQuote.getId())
            .userId(rawQuote.getUserId())
            .text(rawQuote.getText())
            .mentionedIds(rawQuote.getMentionedIds())
            .mentionedUsers(new HashSet<>())
            .quoteTargetId(rawQuote.getReference())
            .hashTags(rawQuote.getHashtags())
            .hashTagSearchField("," + String.join(",", rawQuote.getHashtags()) + ",")
            .build();
  }

  public PublicTweet toPublicTweet() {
    return PublicTweet.builder()
            .type(TWEET)
            .id(this.id.toString())
            .text(this.text)
            .build();
  }

  public void addMentionedUser(final User mentionedUser) {
    if (this.mentionedUsers == null) {
      this.mentionedUsers = new HashSet<>();
    }

    this.mentionedUsers.add(mentionedUser);
  }
}
