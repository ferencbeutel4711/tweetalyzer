package de.fbeutel.tweetalyzer.rawdata.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.fbeutel.tweetalyzer.common.domain.ImportRunningException;
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
import java.util.stream.Stream;
import java.util.zip.GZIPInputStream;

@Slf4j
@Service
public class RawDataService {

  private final RawQuoteRepository rawQuoteRepository;
  private final RawReplyRepository rawReplyRepository;
  private final RawRetweetRepository rawRetweetRepository;
  private final RawStatusRepository rawStatusRepository;
  private final RawUserRepository rawUserRepository;

  public RawDataService(final RawQuoteRepository rawQuoteRepository, final RawReplyRepository rawReplyRepository,
                        final RawRetweetRepository rawRetweetRepository, final RawStatusRepository rawStatusRepository,
                        final RawUserRepository rawUserRepository) {
    this.rawQuoteRepository = rawQuoteRepository;
    this.rawReplyRepository = rawReplyRepository;
    this.rawRetweetRepository = rawRetweetRepository;
    this.rawStatusRepository = rawStatusRepository;
    this.rawUserRepository = rawUserRepository;

  }

  public Stream<RawUser> getAllUsers() {
    return rawUserRepository.findAllAsStream();
  }

  public Long getUsersSize() {
    return rawUserRepository.count();
  }

  public Stream<RawStatus> getAllStatus() {
    return rawStatusRepository.findAllAsStream();
  }

  public Long getStatusSize() {
    return rawStatusRepository.count();
  }

  public List<RawStatus> getAllStatusByUserId(final String userId) {
    return rawStatusRepository.findAllByUserId(userId);
  }

  public Stream<RawRetweet> getAllRetweets() {
    return rawRetweetRepository.findAllAsStream();
  }

  public Long getRetweetSize() {
    return rawRetweetRepository.count();
  }

  public List<RawRetweet> getAllRetweetsByUserId(final String userId) {
    return rawRetweetRepository.findAllByUserId(userId);
  }

  public List<RawRetweet> getAllRetweetsByReference(final String reference) {
    return rawRetweetRepository.findAllByReference(reference);
  }

  public Stream<RawReply> getAllReplies() {
    return rawReplyRepository.findAllAsStream();
  }

  public Long getReplySize() {
    return rawReplyRepository.count();
  }

  public Long getQuotesSize() {
    return rawQuoteRepository.count();
  }
}
