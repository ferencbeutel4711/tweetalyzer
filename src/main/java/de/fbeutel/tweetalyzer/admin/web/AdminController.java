package de.fbeutel.tweetalyzer.admin.web;

import de.fbeutel.tweetalyzer.admin.domain.GraphStatusResponse;
import de.fbeutel.tweetalyzer.admin.domain.RawDataStatusResponse;
import de.fbeutel.tweetalyzer.common.exception.NotFoundException;
import de.fbeutel.tweetalyzer.job.domain.Job;
import de.fbeutel.tweetalyzer.job.domain.JobInformation;
import de.fbeutel.tweetalyzer.job.domain.JobName;
import de.fbeutel.tweetalyzer.job.exception.JobRunningException;
import de.fbeutel.tweetalyzer.graph.service.GraphImportService;
import de.fbeutel.tweetalyzer.graph.service.TweetService;
import de.fbeutel.tweetalyzer.graph.service.UserService;
import de.fbeutel.tweetalyzer.job.service.JobService;
import de.fbeutel.tweetalyzer.rawdata.service.RawDataImportService;
import de.fbeutel.tweetalyzer.rawdata.service.RawDataService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin")
public class AdminController {

  private final RawDataImportService rawDataImportService;
  private final RawDataService rawDataService;
  private final GraphImportService graphImportService;
  private final UserService userService;
  private final TweetService tweetService;
  private final JobService jobService;

  public AdminController(RawDataImportService rawDataImportService, RawDataService rawDataService,
                         GraphImportService graphImportService, UserService userService, TweetService tweetService,
                         JobService jobService) {
    this.rawDataImportService = rawDataImportService;
    this.rawDataService = rawDataService;
    this.graphImportService = graphImportService;
    this.userService = userService;
    this.tweetService = tweetService;
    this.jobService = jobService;
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
            .userNodeCount(userService.countUserNodes())
            .tweetNodeCount(tweetService.countTweetNodes())
            .repliesToRelCount(tweetService.countRepliesToRels())
            .mentionsRelCount(tweetService.countMentionsRels())
            .tweetsRelCount(userService.countTweetsRels())
            .retweetsRelCount(userService.countReTweetsRels())
            .build());
  }

  @GetMapping("/job/{name}")
  public ResponseEntity<JobInformation> getJobInfo(@PathVariable("name") final JobName jobName) {
    return ResponseEntity.ok(jobService.getJobInfo(jobName).orElseThrow(NotFoundException::new).toJobInformation());
  }

  @PostMapping("/import/rawData/start")
  public ResponseEntity<String> startRawDataImport() {
    try {
      rawDataImportService.startMongoImport();
    } catch (JobRunningException e) {
      return ResponseEntity.badRequest().body("Bad Request: Raw data import is already running!");
    }

    return ResponseEntity.ok("Raw Data import started.");
  }

  @PostMapping("/import/graph/user/start")
  public ResponseEntity<Job> startGraphUserImport() {
    return ResponseEntity.ok(graphImportService.startUserImport());
  }

  @PostMapping("/import/graph/tweet/start")
  public ResponseEntity<Job> startGraphTweetImport() {
    return ResponseEntity.ok(graphImportService.startTweetImport());
  }

  @PostMapping("/import/graph/reTweet/start")
  public ResponseEntity<Job> startGraphReTweetImport() {
    return ResponseEntity.ok(graphImportService.startReTweetImport());
  }

  @PostMapping("/import/graph/reply/start")
  public ResponseEntity<Job> startGraphReplyImport() {
    return ResponseEntity.ok(graphImportService.startReplyImport());
  }
}
