package de.fbeutel.tweetalyzer.admin.web;

import de.fbeutel.tweetalyzer.admin.model.GraphStatusResponse;
import de.fbeutel.tweetalyzer.admin.model.RawDataStatusResponse;
import de.fbeutel.tweetalyzer.common.domain.ImportRunningException;
import de.fbeutel.tweetalyzer.graph.service.GraphImportService;
import de.fbeutel.tweetalyzer.graph.service.GraphService;
import de.fbeutel.tweetalyzer.rawdata.service.RawDataImportService;
import de.fbeutel.tweetalyzer.rawdata.service.RawDataService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/admin")
public class AdminController {

  private final RawDataImportService rawDataImportService;
  private final RawDataService rawDataService;
  private final GraphImportService graphImportService;
  private final GraphService graphService;

  public AdminController(RawDataImportService rawDataImportService, RawDataService rawDataService,
                         GraphImportService graphImportService, GraphService graphService) {
    this.rawDataImportService = rawDataImportService;
    this.rawDataService = rawDataService;
    this.graphImportService = graphImportService;
    this.graphService = graphService;
  }

  @GetMapping("/status/rawData")
  public ResponseEntity<RawDataStatusResponse> getRawDataStatus() {
    return ResponseEntity.ok(RawDataStatusResponse.builder()
            .quoteCount(rawDataService.getQuotesSize())
            .replyCount(rawDataService.getReplySize())
            .retweetCount(rawDataService.getRetweetSize())
            .statusCount(rawDataService.getStatusSize())
            .userCount(rawDataService.getUsersSize())
            .build());
  }

  @GetMapping("/status/graph")
  public ResponseEntity<GraphStatusResponse> getGraphStatus() {
    return ResponseEntity.ok(GraphStatusResponse.builder()
            .userNodeCount(graphService.countUserNodes())
            .tweetNodeCount(graphService.countTweetNodes())
            .repliesToRelCount(graphService.countRepliesToRels())
            .mentionsRelCount(graphService.countMentionsRels())
            .tweetsRelCount(graphService.countTweetsRels())
            .retweetsRelCount(graphService.countReTweetsRels())
            .build());
  }

  @PostMapping("/import/rawData/start")
  public ResponseEntity<String> startRawDataImport() {
    try {
      rawDataImportService.startMongoImport();
    } catch (ImportRunningException e) {
      return ResponseEntity.badRequest().body("Bad Request: Raw data import is already running!");
    }

    return ResponseEntity.ok("Raw Data import started.");
  }

  @PostMapping("/import/graph/user/start")
  public ResponseEntity<String> startGraphUserImport() {
    try {
      graphImportService.startUserImport();
    } catch (ImportRunningException e) {
      return ResponseEntity.badRequest().body("Bad Request: A graph import is already running!");
    }

    return ResponseEntity.ok("User graph import started.");
  }

  @PostMapping("/import/graph/tweet/start")
  public ResponseEntity<String> startGraphTweetImport() {
    try {
      graphImportService.startTweetImport();
    } catch (ImportRunningException e) {
      return ResponseEntity.badRequest().body("Bad Request: A graph import is already running!");
    }

    return ResponseEntity.ok("Tweet graph import started.");
  }

  @PostMapping("/import/graph/reTweet/start")
  public ResponseEntity<String> startGraphReTweetImport() {
    try {
      graphImportService.startReTweetImport();
    } catch (ImportRunningException e) {
      return ResponseEntity.badRequest().body("Bad Request: A graph import is already running!");
    }

    return ResponseEntity.ok("ReTweet graph import started.");
  }

  @PostMapping("/import/graph/reply/start")
  public ResponseEntity<String> startGraphReplyImport() {
    try {
      graphImportService.startReplyImport();
    } catch (ImportRunningException e) {
      return ResponseEntity.badRequest().body("Bad Request: A graph import is already running!");
    }

    return ResponseEntity.ok("Reply graph import started.");
  }
}
