package de.fbeutel.tweetalyzer.rawdata.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.apache.commons.compress.archivers.zip.ZipArchiveInputStream;
import org.apache.commons.compress.utils.IOUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.*;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Stream;
import java.util.zip.GZIPInputStream;

@Slf4j
@Service
public class RawDataImportService {

  private final File zipArchive;
  private final ObjectMapper mapper;

  public RawDataImportService(@Value("${zip-archive.path}") final String zipArchivePath, ObjectMapper mapper) {
    this.mapper = mapper;

    try {
      this.zipArchive = new ClassPathResource(zipArchivePath).getFile();
    } catch (IOException exception) {
      log.error("error while unzipping base file", exception);
      throw new RuntimeException(exception);
    }
  }

  public Stream<Map<String, Object>> getRawData() {
    try (final ZipArchiveInputStream zip = new ZipArchiveInputStream(new FileInputStream(zipArchive))) {
      final Map<String, byte[]> zippedFiles = new ConcurrentHashMap<>();

      ZipArchiveEntry zipEntry = zip.getNextZipEntry();
      while (zipEntry != null) {
        zippedFiles.put(zipEntry.getName(), IOUtils.toByteArray(zip));
        zipEntry = zip.getNextZipEntry();
      }

      return zippedFiles.entrySet()
              .stream()
              .parallel()
              .flatMap(entry -> {
                try {
                  final InputStream rawJson = new GZIPInputStream(new ByteArrayInputStream(entry.getValue()));
                  final List<Map<String, Object>> rawEntries = mapper.readValue(rawJson, new TypeReference<List<Map<String,
                          Object>>>() {
                  });
                  return rawEntries.stream();
                } catch (IOException exception) {
                  log.error("error while g-unzipping inner file: " + entry.getKey(), exception);
                  throw new RuntimeException(exception);
                }
              });
    } catch (final IOException exception) {
      log.error("error while unzipping base file", exception);
      throw new RuntimeException(exception);
    }
  }

  public long getAmountOfArchives() {
    return this.getRawData().count();
  }
}
