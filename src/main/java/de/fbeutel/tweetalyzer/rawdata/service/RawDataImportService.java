package de.fbeutel.tweetalyzer.rawdata.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.fbeutel.tweetalyzer.job.domain.Job;
import de.fbeutel.tweetalyzer.job.service.JobService;
import de.fbeutel.tweetalyzer.rawdata.domain.*;
import de.fbeutel.tweetalyzer.rawdata.job.RawDataImportJob;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.apache.commons.compress.archivers.zip.ZipArchiveInputStream;
import org.apache.commons.compress.utils.IOUtils;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.*;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Service
public class RawDataImportService {

  private static final String ZIP_ARCHIVE_NAME = "data/german-tweet-sample-2019-04.zip";

  private final RawQuoteRepository rawQuoteRepository;
  private final RawReplyRepository rawReplyRepository;
  private final RawRetweetRepository rawRetweetRepository;
  private final RawStatusRepository rawStatusRepository;
  private final RawUserRepository rawUserRepository;

  private final Map<String, byte[]> archives;

  public RawDataImportService(final RawQuoteRepository rawQuoteRepository, final RawReplyRepository rawReplyRepository,
                              final RawRetweetRepository rawRetweetRepository, final RawStatusRepository rawStatusRepository,
                              final RawUserRepository rawUserRepository) {
    this.rawQuoteRepository = rawQuoteRepository;
    this.rawReplyRepository = rawReplyRepository;
    this.rawRetweetRepository = rawRetweetRepository;
    this.rawStatusRepository = rawStatusRepository;
    this.rawUserRepository = rawUserRepository;

    // one could argue that holding this in memory is a bad idea..
    // however, doing the unzipping again for each job restart seems like a waste of time
    try (final ZipArchiveInputStream zip =
                 new ZipArchiveInputStream(new FileInputStream(new ClassPathResource(ZIP_ARCHIVE_NAME).getFile()))) {
      final Map<String, byte[]> zippedFiles = new ConcurrentHashMap<>();

      ZipArchiveEntry entry = zip.getNextZipEntry();
      while (entry != null) {
        zippedFiles.put(entry.getName(), IOUtils.toByteArray(zip));
        entry = zip.getNextZipEntry();
      }

      this.archives = zippedFiles;

    } catch (final IOException exception) {
      log.error("error while unzipping base file", exception);
      throw new RuntimeException(exception);
    }
  }

  public Map<String, byte[]> getArchives() {
    return archives;
  }

  public long getAmountOfArchives() {
    return archives.size();
  }

  public RawQuote save(final RawQuote rawQuote) {
    return rawQuoteRepository.save(rawQuote);
  }

  public RawReply save(final RawReply rawReply) {
    return rawReplyRepository.save(rawReply);
  }

  public RawRetweet save(final RawRetweet rawRetweet) {
    return rawRetweetRepository.save(rawRetweet);
  }

  public RawStatus save(final RawStatus rawStatus) {
    return rawStatusRepository.save(rawStatus);
  }

  public RawUser save(final RawUser rawUser) {
    return rawUserRepository.save(rawUser);
  }
}
