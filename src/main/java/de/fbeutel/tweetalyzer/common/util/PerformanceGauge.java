package de.fbeutel.tweetalyzer.common.util;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class PerformanceGauge {

  private final String context;
  private final int amountOfWork;
  private final double thresholdPercentage;

  private int completionCount = 0;
  private int loggedCount = 0;

  private long startTimeStamp;

  public PerformanceGauge(final String context, final int amountOfWork, final double thresholdPercentage) {
    this.context = context;
    this.amountOfWork = amountOfWork;
    this.thresholdPercentage = thresholdPercentage;

    logSetup();
  }

  public void start() {
    this.startTimeStamp = System.currentTimeMillis();
  }

  public void reportCompletion(final int completionCount) {
    this.completionCount += completionCount;

    if ((double) this.completionCount / this.amountOfWork * 100 >= this.thresholdPercentage * this.loggedCount) {
      logStep();
      if (this.amountOfWork <= this.completionCount) {
        logCompletion();
        if (this.completionCount > this.amountOfWork) {
          log.warn("performance Gauge " + this.context + " saw more completions than amount of work!..");
        }
      }
    }
  }

  private void logSetup() {
    log.info("new Performance Gauge initialized! context: " + this.context + " amount of work: " + this.amountOfWork + " will " +
            "log every: " + this.thresholdPercentage + "%");
    this.loggedCount++;
  }

  private void logStep() {
    log.info("Performance Gauge " + this.context + " is " + this.loggedCount * this.thresholdPercentage + "% completed");
    this.loggedCount++;
  }

  private void logCompletion() {
    final long endTime = System.currentTimeMillis();
    log.info("Performance Gauge " + this.context + " finished! It took: " + (endTime - this.startTimeStamp) / 1000.0 + "s");
  }
}
