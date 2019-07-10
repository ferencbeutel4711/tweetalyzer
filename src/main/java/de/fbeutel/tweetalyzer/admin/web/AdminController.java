package de.fbeutel.tweetalyzer.admin.web;

import de.fbeutel.tweetalyzer.common.domain.ImportRunningException;
import de.fbeutel.tweetalyzer.graph.service.GraphImportService;
import de.fbeutel.tweetalyzer.rawdata.service.RawDataImportService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/admin")
public class AdminController {

  private final RawDataImportService rawDataImportService;
  private final GraphImportService graphImportService;

  public AdminController(RawDataImportService rawDataImportService, GraphImportService graphImportService) {
    this.rawDataImportService = rawDataImportService;
    this.graphImportService = graphImportService;
  }

  @GetMapping("/import/rawData/start")
  public ResponseEntity<String> startRawDataImport() {
    try {
      rawDataImportService.startMongoImport();
    } catch (ImportRunningException e) {
      return ResponseEntity.badRequest().body("Bad Request: Raw data import is already running!");
    }

    return ResponseEntity.ok("Raw Data import started.");
  }

  @GetMapping("/import/graph/user/start")
  public ResponseEntity<String> startGraphUserImport() {
    try {
      graphImportService.startUserImport();
    } catch (ImportRunningException e) {
      return ResponseEntity.badRequest().body("Bad Request: A graph import is already running!");
    }

    return ResponseEntity.ok("User graph import started.");
  }

  @GetMapping("/import/graph/tweet/start")
  public ResponseEntity<String> startGraphTweetImport() {
    try {
      graphImportService.startTweetImport();
    } catch (ImportRunningException e) {
      return ResponseEntity.badRequest().body("Bad Request: A graph import is already running!");
    }

    return ResponseEntity.ok("Tweet graph import started.");
  }

  @GetMapping("/import/graph/reTweet/start")
  public ResponseEntity<String> startGraphReTweetImport() {
    try {
      graphImportService.startReTweetImport();
    } catch (ImportRunningException e) {
      return ResponseEntity.badRequest().body("Bad Request: A graph import is already running!");
    }

    return ResponseEntity.ok("ReTweet graph import started.");
  }

  @GetMapping("/import/graph/reply/start")
  public ResponseEntity<String> startGraphReplyImport() {
    try {
      graphImportService.startReplyImport();
    } catch (ImportRunningException e) {
      return ResponseEntity.badRequest().body("Bad Request: A graph import is already running!");
    }

    return ResponseEntity.ok("Reply graph import started.");
  }
}
