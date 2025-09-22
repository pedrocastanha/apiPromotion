package org.example.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

public class CustomExceptions {
   @ResponseStatus(HttpStatus.NOT_FOUND)
   public static class ResourceNotFoundException extends RuntimeException {
      public ResourceNotFoundException(String message) {
         super(message);
      }

      public ResourceNotFoundException(String resource, String field, Object value) {
         super(String.format("%s não encontrado com %s: %s", resource, field, value));
      }
   }

   @ResponseStatus(HttpStatus.BAD_REQUEST)
   public static class InvalidDataException extends RuntimeException {
      public InvalidDataException(String message) {
         super(message);
      }
   }

   @ResponseStatus(HttpStatus.CONFLICT)
   public static class DataConflictException extends RuntimeException {
      public DataConflictException(String message) {
         super(message);
      }
   }

   @ResponseStatus(HttpStatus.UNAUTHORIZED)
   public static class AuthenticationException extends RuntimeException {
      public AuthenticationException(String message) {
         super(message);
      }
   }

   @ResponseStatus(HttpStatus.FORBIDDEN)
   public static class AuthorizationException extends RuntimeException {
      public AuthorizationException(String message) {
         super(message);
      }
   }

   @ResponseStatus(HttpStatus.BAD_REQUEST)
   public static class FileUploadException extends RuntimeException {
      public FileUploadException(String message) {
         super(message);
      }
   }

   @ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
   public static class CampaignProcessingException extends RuntimeException {
      public CampaignProcessingException(String message) {
         super(message);
      }
   }

   @ResponseStatus(HttpStatus.TOO_MANY_REQUESTS)
   public static class RateLimitExceededException extends RuntimeException {
      public RateLimitExceededException(String message) {
         super(message);
      }
   }
}