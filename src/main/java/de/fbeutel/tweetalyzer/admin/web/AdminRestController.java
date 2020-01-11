package de.fbeutel.tweetalyzer.admin.web;

import de.fbeutel.tweetalyzer.admin.domain.GraphStatusResponse;
import de.fbeutel.tweetalyzer.admin.domain.RawDataStatusResponse;
import de.fbeutel.tweetalyzer.common.exception.NotFoundException;
import de.fbeutel.tweetalyzer.job.domain.Job;
import de.fbeutel.tweetalyzer.job.domain.JobInformation;
import de.fbeutel.tweetalyzer.job.domain.JobName;
import de.fbeutel.tweetalyzer.graph.service.TweetService;
import de.fbeutel.tweetalyzer.graph.service.UserService;
import de.fbeutel.tweetalyzer.job.service.JobService;
import de.fbeutel.tweetalyzer.rawdata.service.RawDataImportService;
import de.fbeutel.tweetalyzer.rawdata.service.RawDataService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin")
public class AdminRestController {

  private final RawDataService rawDataService;
  private final UserService userService;
  private final TweetService tweetService;
  private final JobService jobService;

  public AdminRestController(RawDataService rawDataService, UserService userService, TweetService tweetService,
                             JobService jobService) {
    this.rawDataService = rawDataService;
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
            .quotesRelCount(tweetService.countQuoteRels())
            .build());
  }

  @GetMapping("/job/{name}")
  public ResponseEntity<JobInformation> getJobInfo(@PathVariable("name") final JobName jobName) {
    return ResponseEntity.ok(jobService.getJobInfo(jobName).orElseThrow(NotFoundException::new).toJobInformation());
  }

  @PostMapping("/job/{name}/start")
  public ResponseEntity<Void> startRawDataImport(@PathVariable("name") final JobName jobName) {
    jobService.startJob(jobName);

    return ResponseEntity.ok().build();
  }
}
