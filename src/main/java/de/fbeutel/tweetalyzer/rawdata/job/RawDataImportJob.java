package de.fbeutel.tweetalyzer.rawdata.job;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.fbeutel.tweetalyzer.job.domain.Job;
import de.fbeutel.tweetalyzer.job.domain.JobName;
import de.fbeutel.tweetalyzer.rawdata.domain.*;
import de.fbeutel.tweetalyzer.rawdata.service.RawDataImportService;
import de.fbeutel.tweetalyzer.rawdata.service.RawDataService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import static de.fbeutel.tweetalyzer.job.domain.JobName.*;
import static de.fbeutel.tweetalyzer.job.domain.JobStatus.INITIAL;

@Slf4j
@Component
public class RawDataImportJob extends Job {

  private static final String JOB_DESCRIPTION = "This Job imports the raw data provided into the mongoDB";
  private static final List<JobName> MUTEX_GROUP = new ArrayList<>();

  private final RawDataImportService rawDataImportService;
  private final RawDataService rawDataService;
  private final ObjectMapper mapper;

  public RawDataImportJob(final RawDataImportService rawDataImportService, final RawDataService rawDataService,
                          final ObjectMapper mapper) {
    super(RAW_DATA_IMPORT_JOB, JOB_DESCRIPTION, MUTEX_GROUP, rawDataImportService::getAmountOfArchives, INITIAL, 0, 0, 0);

    this.rawDataImportService = rawDataImportService;
    this.rawDataService = rawDataService;
    this.mapper = mapper;
  }

  @Override
  protected void execute() {
    this.mapAndPersist(rawDataImportService.getRawData());
  }

  private void mapAndPersist(final Stream<Map<String, Object>> entries) {
    entries.forEach(rawEntry -> {
      final Object rawType = rawEntry.get("type");
      if (rawType != null) {
        final String type = rawType.toString();
        switch (type) {
          case "quote":
            rawDataService.save(mapper.convertValue(rawEntry, RawQuote.class));
            break;
          case "reply":
            rawDataService.save(mapper.convertValue(rawEntry, RawReply.class));
            break;
          case "retweet":
            rawDataService.save(mapper.convertValue(rawEntry, RawRetweet.class));
            break;
          case "status":
            rawDataService.save(mapper.convertValue(rawEntry, RawStatus.class));
            break;
          case "user":
            rawDataService.save(mapper.convertValue(rawEntry, RawUser.class));
            break;
          default:
            log.error("no mapping found for new type: " + type);
        }
      } else {
        log.error("entry found without type information: " + rawEntry.toString());
      }

      this.reportCompletion(1);
    });
  }
}