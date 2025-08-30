package org.example.exception;

public class EvolutionApiException extends RuntimeException {
   private final int statusCode;
   private final String responseBody;

   public EvolutionApiException(String message, int statusCode, String responseBody) {
      super(message);
      this.statusCode = statusCode;
      this.responseBody = responseBody;
   }

   public int getStatusCode() {
      return statusCode;
   }

   public String getResponseBody() {
      return responseBody;
   }

   @Override
   public String getMessage() {
      return String.format("%s (Status: %d, Response: %s)", super.getMessage(), statusCode, responseBody);
   }
}