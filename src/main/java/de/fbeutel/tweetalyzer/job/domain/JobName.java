package de.fbeutel.tweetalyzer.job.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum JobName {
  IMPORT_GRAPH_USERS_JOB("graphUserImport"), IMPORT_GRAPH_TWEETS_JOB("graphTweetImport"), IMPORT_GRAPH_RETWEETS_JOB(
          "graphRetweetImport"), IMPORT_GRAPH_REPLIES_JOB("graphReplyImport"), RAW_DATA_IMPORT_JOB("rawDataImportJob"),
  IMPORT_GRAPH_QUOTES_JOB("graphQuotesImport");

  private final String readableName;
}
