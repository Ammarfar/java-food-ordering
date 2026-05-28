package ammarfar.test.food.ordering.Controller;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import ammarfar.test.food.ordering.Dto.ApiResponse;
import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<ApiResponse<List<Map<String, String>>>> handleMethodArgumentNotValid(
      MethodArgumentNotValidException ex) {
    List<Map<String, String>> errors = ex.getBindingResult().getFieldErrors().stream()
        .map(this::toFieldError)
        .toList();
    return ResponseEntity.badRequest().body(ApiResponse.of("Validation failed", errors));
  }

  @ExceptionHandler(ConstraintViolationException.class)
  public ResponseEntity<ApiResponse<List<Map<String, String>>>> handleConstraintViolation(
      ConstraintViolationException ex) {
    List<Map<String, String>> errors = ex.getConstraintViolations().stream()
        .map(v -> Map.of(
            "field", v.getPropertyPath().toString(),
            "message", v.getMessage()))
        .toList();
    return ResponseEntity.badRequest().body(ApiResponse.of("Validation failed", errors));
  }

  @ExceptionHandler({
      HttpMessageNotReadableException.class,
      MissingServletRequestParameterException.class
  })
  public ResponseEntity<ApiResponse<Void>> handleBadRequest(Exception ex) {
    return ResponseEntity.badRequest().body(ApiResponse.of("Bad request", null));
  }

  @ExceptionHandler(AccessDeniedException.class)
  public ResponseEntity<ApiResponse<Void>> handleAccessDenied(AccessDeniedException ex) {
    return ResponseEntity.status(HttpStatus.FORBIDDEN).body(ApiResponse.of("Forbidden", null));
  }

  @ExceptionHandler(IllegalArgumentException.class)
  public ResponseEntity<ApiResponse<Void>> handleIllegalArgumentException(IllegalArgumentException ex) {
    HttpStatus status = resolveStatus(ex.getMessage());
    return ResponseEntity.status(status).body(ApiResponse.of(ex.getMessage(), null));
  }

  @ExceptionHandler(Exception.class)
  public ResponseEntity<ApiResponse<Void>> handleGeneric(Exception ex) {
    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ApiResponse.of("Internal server error", null));
  }

  private Map<String, String> toFieldError(FieldError error) {
    Map<String, String> fieldError = new LinkedHashMap<>();
    fieldError.put("field", error.getField());
    fieldError.put("message", error.getDefaultMessage());
    return fieldError;
  }

  private HttpStatus resolveStatus(String message) {
    if (message == null) {
      return HttpStatus.BAD_REQUEST;
    }

    String lowerMessage = message.toLowerCase();
    if (lowerMessage.contains("invalid email or password")) {
      return HttpStatus.UNAUTHORIZED;
    }

    if (lowerMessage.contains("not found")) {
      return HttpStatus.NOT_FOUND;
    }

    return HttpStatus.BAD_REQUEST;
  }
}
