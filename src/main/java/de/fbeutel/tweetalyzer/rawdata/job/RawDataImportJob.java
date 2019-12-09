package de.fbeutel.tweetalyzer.rawdata.job;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.fbeutel.tweetalyzer.job.domain.Job;
import de.fbeutel.tweetalyzer.job.domain.JobName;
import de.fbeutel.tweetalyzer.rawdata.domain.*;
import de.fbeutel.tweetalyzer.rawdata.service.RawDataImportService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.zip.GZIPInputStream;

import static de.fbeutel.tweetalyzer.job.domain.JobName.*;
import static de.fbeutel.tweetalyzer.job.domain.JobStatus.INITIAL;

@Slf4j
@Component
public class RawDataImportJob extends Job {

  private static final String JOB_DESCRIPTION = "This Job imports the raw data provided into the mongoDB";
  private static final List<JobName> MUTEX_GROUP = new ArrayList<>();

  private final RawDataImportService rawDataImportService;
  private final ObjectMapper mapper;


  public RawDataImportJob(RawDataImportService rawDataImportService, ObjectMapper mapper) {
    super(RAW_DATA_IMPORT_JOB, JOB_DESCRIPTION, MUTEX_GROUP, rawDataImportService::getAmountOfArchives, INITIAL, 0, 0, 0);

    this.rawDataImportService = rawDataImportService;
    this.mapper = mapper;
  }

  @Override
  protected void execute() {
    rawDataImportService.getArchives().entrySet().stream()
            .parallel()
            .forEach(entry -> gunzipArchiveAndPersist(entry.getKey(), entry.getValue()));
  }

  private void gunzipArchiveAndPersist(final String entryName, final byte[] content) {
    try {
      final InputStream rawJson = new GZIPInputStream(new ByteArrayInputStream(content));
      final List<Map<String, Object>> rawEntries = mapper.readValue(rawJson, new TypeReference<List<Map<String, Object>>>() {
      });

      rawEntries.forEach(rawEntry -> {
        final Object rawType = rawEntry.get("type");
        if (rawType != null) {
          final String type = rawType.toString();
          switch (type) {
            case "quote":
              rawDataImportService.save(mapper.convertValue(rawEntry, RawQuote.class));
              break;
            case "reply":
              rawDataImportService.save(mapper.convertValue(rawEntry, RawReply.class));
              break;
            case "retweet":
              rawDataImportService.save(mapper.convertValue(rawEntry, RawRetweet.class));
              break;
            case "status":
              rawDataImportService.save(mapper.convertValue(rawEntry, RawStatus.class));
              break;
            case "user":
              rawDataImportService.save(mapper.convertValue(rawEntry, RawUser.class));
              break;
            default:
              log.error("no mapping found for new type: " + type);
          }
        } else {
          log.error("entry found without type information: " + rawEntry.toString());
        }
      });
    } catch (final IOException exception) {
      log.error("error during gzip entry processing. entry: " + entryName, exception);
    } finally {
      this.reportCompletion(1);
    }
  }
}