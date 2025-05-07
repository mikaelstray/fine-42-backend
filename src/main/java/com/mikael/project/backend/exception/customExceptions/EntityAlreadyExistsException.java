package com.mikael.project.backend.exception.customExceptions;

import com.mikael.project.backend.exception.CustomErrorMessage;
import lombok.Getter;

/**
 * Exception thrown when an attempt is made to create an entity that already exists.
 * <p>
 * Used to signal duplication conflicts when an entity with the same identifier or properties
 * already exists in the system.
 * Contains a {@link CustomErrorMessage} providing detailed error information.
 * </p>
 */
@Getter
public class EntityAlreadyExistsException extends RuntimeException {

  /**
   * Custom error message containing details about the exception.
   */
  private final CustomErrorMessage errorMessage;

  /**
   * Constructs a new {@code EntityAlreadyExistsException} with the specified error message.
   *
   * @param errorMessage the detailed custom error message
   */
  public EntityAlreadyExistsException(CustomErrorMessage errorMessage) {
    super(errorMessage.getMessage());
    this.errorMessage = errorMessage;
  }
}
