package de.fbeutel.tweetalyzer.common.web.advice;

import de.fbeutel.tweetalyzer.common.exception.NotFoundException;
import de.fbeutel.tweetalyzer.job.exception.JobInRunningMutexGroupException;
import de.fbeutel.tweetalyzer.job.exception.JobRunningException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

import static org.springframework.http.HttpStatus.*;

@ControllerAdvice
public class ExceptionHandlerControllerAdvice {

  @ExceptionHandler(NotFoundException.class)
  @ResponseStatus(NOT_FOUND)
  public void notFound() {
  }

  @ExceptionHandler(JobRunningException.class)
  @ResponseStatus(LOCKED)
  public void locked() {
  }

  @ExceptionHandler(JobInRunningMutexGroupException.class)
  @ResponseStatus(CONFLICT)
  public void conflict() {
  }
}
