package de.fbeutel.tweetalyzer.rawdata.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.fbeutel.tweetalyzer.rawdata.domain.MongoImportRunningException;
import de.fbeutel.tweetalyzer.rawdata.domain.RawStatus;
import de.fbeutel.tweetalyzer.rawdata.domain.RawStatusRepository;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.apache.commons.compress.archivers.zip.ZipArchiveInputStream;
import org.apache.commons.compress.utils.IOUtils;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.*;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.zip.GZIPInputStream;

@Slf4j
@Service
public class RawStatusService {

  private final RawStatusRepository rawStatusRepository;

  public RawStatusService(RawStatusRepository rawStatusRepository, final ObjectMapper mapper) throws IOException {
    this.rawStatusRepository = rawStatusRepository;
  }

  public RawStatus save(final RawStatus rawStatus) {
    return rawStatusRepository.save(rawStatus);
  }
}
