package de.fbeutel.tweetalyzer.admin.web;

import de.fbeutel.tweetalyzer.rawdata.domain.MongoImportRunningException;
import de.fbeutel.tweetalyzer.rawdata.service.RawDataService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/admin")
public class AdminController {

  private final RawDataService rawDataService;

  public AdminController(RawDataService rawDataService) {
    this.rawDataService = rawDataService;
  }

  @GetMapping("/startMongoImport")
  public ResponseEntity<String> startMongoImport() {
    try {
      rawDataService.startMongoImport();
    } catch (MongoImportRunningException e) {
      return ResponseEntity.badRequest().body("Bad Request: Mongo Import already running!");
    }

    return ResponseEntity.ok("MongoDB Import started.");
  }
}
