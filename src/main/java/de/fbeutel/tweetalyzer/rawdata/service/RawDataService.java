package de.fbeutel.tweetalyzer.rawdata.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.fbeutel.tweetalyzer.common.util.PerformanceGauge;
import de.fbeutel.tweetalyzer.rawdata.domain.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.apache.commons.compress.archivers.zip.ZipArchiveInputStream;
import org.apache.commons.compress.utils.IOUtils;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.zip.GZIPInputStream;

@Slf4j
@Service
public class RawDataService {

  private static final String ZIP_ARCHIVE_NAME = "data/german-tweet-sample-2019-04.zip";

  private final RawQuoteRepository rawQuoteRepository;
  private final RawReplyRepository rawReplyRepository;
  private final RawRetweetRepository rawRetweetRepository;
  private final RawStatusService rawStatusService;
  private final RawUserRepository rawUserRepository;
  private final ObjectMapper mapper;

  private final ExecutorService dataImportExecutionService;
  private final AtomicBoolean importRunning;
  private final File zipArchive;

  private final List<String> knownNewTypes = new ArrayList<>();

  public RawDataService(final RawQuoteRepository rawQuoteRepository, final RawReplyRepository rawReplyRepository,
                        final RawRetweetRepository rawRetweetRepository, final RawStatusService rawStatusService,
                        final RawUserRepository rawUserRepository, final ObjectMapper mapper) throws IOException {
    this.rawQuoteRepository = rawQuoteRepository;
    this.rawReplyRepository = rawReplyRepository;
    this.rawRetweetRepository = rawRetweetRepository;
    this.rawStatusService = rawStatusService;
    this.rawUserRepository = rawUserRepository;
    this.mapper = mapper;

    dataImportExecutionService = Executors.newFixedThreadPool(10);
    this.importRunning = new AtomicBoolean(false);
    this.zipArchive = new ClassPathResource(ZIP_ARCHIVE_NAME).getFile();
  }

  public void startMongoImport() throws MongoImportRunningException {
    if (importRunning.get()) {
      throw new MongoImportRunningException();
    }

    importRunning.set(true);
    try {
      log.info("starting new mongo import");
      unzipArchiveAndProcess();
      log.info("mongo import started");
    } finally {
      importRunning.set(false);
    }
  }

  private void unzipArchiveAndProcess() {
    try (final ZipArchiveInputStream zip = new ZipArchiveInputStream(new FileInputStream(zipArchive))) {
      final Map<String, byte[]> zippedFiles = new ConcurrentHashMap<>();

      ZipArchiveEntry entry = zip.getNextZipEntry();
      while (entry != null) {
        zippedFiles.put(entry.getName(), IOUtils.toByteArray(zip));
        entry = zip.getNextZipEntry();
      }

      final PerformanceGauge performanceGauge = new PerformanceGauge("RawDataService", zippedFiles.size(), 10.0);

      performanceGauge.start();

      zippedFiles.forEach((entryName, input) -> gunzipArchiveAndPersist(entryName, input, performanceGauge));

    } catch (final IOException exception) {
      log.error("error while unzipping base file", exception);
      throw new RuntimeException(exception);
    }
  }

  private void gunzipArchiveAndPersist(final String entryName, final byte[] content, final PerformanceGauge performanceGauge) {
    dataImportExecutionService.submit(() -> {
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
                rawQuoteRepository.save(mapper.convertValue(rawEntry, RawQuote.class));
                break;
              case "reply":
                rawReplyRepository.save(mapper.convertValue(rawEntry, RawReply.class));
                break;
              case "retweet":
                rawRetweetRepository.save(mapper.convertValue(rawEntry, RawRetweet.class));
                break;
              case "status":
                rawStatusService.save(mapper.convertValue(rawEntry, RawStatus.class));
                break;
              case "user":
                rawUserRepository.save(mapper.convertValue(rawEntry, RawUser.class));
                break;
              default:
                if (!knownNewTypes.contains(type)) {
                  log.warn("new type found: " + type);
                  knownNewTypes.add(type);
                }
            }
          } else {
            log.error("entry found without type information: " + rawEntry.toString());
          }
        });
      } catch (final IOException exception) {
        log.error("error during gzip entry processing. entry: " + entryName, exception);
      }

      performanceGauge.reportCompletion(1);
    });
  }
}
