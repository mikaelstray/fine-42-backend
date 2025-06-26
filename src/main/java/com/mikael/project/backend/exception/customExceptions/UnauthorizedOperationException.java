package com.mikael.project.backend.exception.customExceptions;

import com.mikael.project.backend.exception.CustomErrorMessage;
import lombok.Getter;

/**
 * Exception for unauthorized operations by non-admin users.
 */

@Getter
public class UnauthorizedOperationException extends RuntimeException {

  private final CustomErrorMessage errorMessage;

  /**
   * Creates a new exception with the given error message.
   *
   * @param errorMessage the error message enum
   */
  public UnauthorizedOperationException(CustomErrorMessage errorMessage) {
    super(errorMessage.getMessage());
    this.errorMessage = errorMessage;
  }
  }