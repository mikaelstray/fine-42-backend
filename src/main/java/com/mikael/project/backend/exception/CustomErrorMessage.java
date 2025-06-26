package com.mikael.project.backend.exception;

import lombok.Getter;

/**
 * Enum representing custom error messages and their associated HTTP status codes.
 * <p>
 * Used throughout the application to provide consistent error handling and responses.
 * Each constant defines both a status code and a descriptive message.
 * </p>
 */
@Getter
public enum CustomErrorMessage {

  // --- User Errors ---
  USERNAME_ALREADY_EXISTS    (409, "Username already exists."),
  USER_NOT_FOUND             (404, "User not found."),

  // --- Fine Errors ---
  FINE_NOT_FOUND             (404, "Fine not found."),
  INVALID_FINE_DATA          (400, "Fine data is invalid."),

  // --- Household errors ---
  HOUSEHOLD_NOT_FOUND (404, "Household not found"),
  NOT_IN_HOUSEHOLD(404, "User is not in household"),

  // --- Authorization ---
  UNAUTHORIZED_OPERATION     (403, "You are not authorized to perform this operation."),

  // --- Generic ---
  INTERNAL_SERVER_ERROR      (500, "An unexpected internal server error occurred.");


  /**
   * The HTTP status code associated with the error.
   */
  private final int status;

  /**
   * The descriptive error message.
   */
  private final String message;

  CustomErrorMessage(int status, String message) {
    this.status = status;
    this.message = message;
  }
}
