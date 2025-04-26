package com.readify.example.exception;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.io.FileNotFoundException;
import java.net.URI;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);


    /**
     * Handles {@link ConstraintViolationException} by extracting all violation messages
     * and concatenating them into a single error message. Returns a standardized error
     * response with a 400 Bad Request status code.
     *
     * @param ex the exception to handle
     * @return a response entity containing the error message and a bad request status
     */

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<?> handleConstraintViolationException(ConstraintViolationException ex) {
        String errorMessage = ex.getConstraintViolations()
                .stream()
                .map(ConstraintViolation::getMessage)
                .reduce((msg1, msg2) -> msg1 + "; " + msg2)
                .orElse("Validation failed.");

        return CommonUtils.createErrorResponseMessage(errorMessage, HttpStatus.BAD_REQUEST);
    }

    /**
     * Handle {@link NoSuchFieldException} by logging the exception details and returning
     * a standardized error response with a 400 status code.
     *
     * @param ex the exception to handle
     * @return a standardized error response
     */
    @ExceptionHandler(NoSuchFieldException.class)
    public ResponseEntity<?> handleNoSuchFieldException(NoSuchFieldException ex) {
        // Log the exception details
        logExceptionDetails(ex);

        // Use CommonUtils to create a standardized error response
        return CommonUtils.createErrorResponseMessage(
                "The requested field does not exist: " + ex.getMessage(),
                HttpStatus.BAD_REQUEST
        );
    }

    /**
     * Handles {@link MethodArgumentNotValidException} by logging the exception details
     * and extracting field errors into a list of maps. Each map contains information
     * about the invalid field and the corresponding error message. Returns a standardized
     * error response with a 400 Bad Request status code.
     *
     * @param ex the exception to handle
     * @return a response entity containing the field errors and a bad request status
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<?> handleValidationExceptions(MethodArgumentNotValidException ex) {
        logExceptionDetails(ex);

        List<Map<String, String>> errors = ex.getBindingResult().getFieldErrors().stream()
                .map(error -> Map.of(
                        "field", error.getField(),
                        "message", error.getDefaultMessage()
                ))
                .collect(Collectors.toList());

        // Use CommonUtils for consistent error response
        return CommonUtils.createErrorResponse(errors.get(0), HttpStatus.BAD_REQUEST);// here we are not sending  all the exception list we are sending the 0 index data
    }

    @ExceptionHandler(NullPointerException.class)
    public ResponseEntity<?> handleNullPointerException(Exception e) {
        logExceptionDetails(e);
        return CommonUtils.createErrorResponseMessage(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    /**
     * Handles {@link ValidationException} by logging the exception details and
     * extracting the {@link ValidationException#getErrors() errors} into a list of maps.
     * Each map contains information about the invalid field and the corresponding error
     * message. Returns a standardized error response with a 404 Not Found status code.
     *
     * @param e the exception to handle
     * @return a response entity containing the field errors and a not found status
     */
    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<?> handleValException(ValidationException e) {
        logExceptionDetails(e);
        return CommonUtils.createErrorResponse(e.getErrors(), HttpStatus.NOT_FOUND);
    }

    /**
     * Handles {@link FileNotFoundException} by logging the exception details and
     * returning a standardized error response with a 404 Not Found status code.
     *
     * @param e the exception to handle
     * @return a response entity containing the error message and a not found status
     */
    @ExceptionHandler(FileNotFoundException.class)
    public ResponseEntity<?> handleFileNotFoundException(FileNotFoundException e) {
        logExceptionDetails(e);
        return CommonUtils.createErrorResponse(e.getMessage(), HttpStatus.NOT_FOUND);
    }

    /**
     * Handles {@link ExistDataException} by logging the exception details and
     * returning a standardized error response with a 409 Conflict status code.
     *
     * @param e the exception to handle
     * @return a response entity containing the error message and a conflict status
     */
    @ExceptionHandler(ExistDataException.class)
    public ResponseEntity<?> handleExistDataException(ExistDataException e) {
        logExceptionDetails(e);
        return CommonUtils.createErrorResponse(e.getMessage(), HttpStatus.CONFLICT);
    }
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<String> handleTypeMismatch(MethodArgumentTypeMismatchException ex) {
        String paramName = ex.getName();
        String receivedValue = (ex.getValue() != null) ? ex.getValue().toString() : "null";
        String requiredType = (ex.getRequiredType() != null) ? ex.getRequiredType().getSimpleName() : "unknown";
        String message = "Invalid value for '" + paramName + "'. Expected type: " + requiredType + ", but received: '" + receivedValue + "'";
        return new ResponseEntity<>(message, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(DateTimeParseException.class)
    public ResponseEntity<String> handleDateParseException(DateTimeParseException ex) {
        return new ResponseEntity<>("Invalid date format. Please use 'yyyy-MM-dd'.", HttpStatus.BAD_REQUEST);
    }
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<?> handleHttpMessageNotReadableException(HttpMessageNotReadableException e) {
        logExceptionDetails(e);
        return CommonUtils.createErrorResponse(e.getMessage(), HttpStatus.BAD_REQUEST);
    }

    /**
     * Handles {@link BackendException} by creating a {@link ProblemDetail} object
     * with the appropriate HTTP status, title, detail, and type based on the exception's
     * error code and message. Returns a response entity containing the problem detail
     * and the corresponding HTTP status code.
     *
     * @param e the {@link BackendException} to handle
     * @return a response entity with a {@link ProblemDetail} and the appropriate status code
     */
    @ExceptionHandler(BackendException.class)
    public ResponseEntity<ProblemDetail> designerExceptionHandler(BackendException e) {
        ProblemDetail problemDetail = ProblemDetail.forStatus(e.getErrorCode().getHttpStatus());
        problemDetail.setTitle("Something Went Wrong");
        problemDetail.setDetail("error is :->" + e.getMessage());
        problemDetail.setType(URI.create(e.getErrorCode().name()));
        return ResponseEntity.status(e.getErrorCode().getHttpStatus()).body(problemDetail);

    }

    /**
     * Handles any uncaught exceptions by logging the exception details and
     * returning a standardized error response with a 500 status code.
     *
     * @param e the exception to handle
     * @return a standardized error response
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> handleException(Exception e) {
        System.out.println(e.getMessage());
        logExceptionDetails(e);
        return CommonUtils.createErrorResponseMessage(e.getMessage(), HttpStatus.BAD_REQUEST);
    }

    /**
     * Logs the details of the given exception, including the class and method
     * where the exception originated, along with the exception message.
     * If the stack trace is empty, logs the exception as unhandled.
     *
     * @param e the exception whose details are to be logged
     */
    private void logExceptionDetails(Exception e) {
        StackTraceElement[] stackTrace = e.getStackTrace();
        if (stackTrace.length > 0) {
            StackTraceElement origin = stackTrace[0];
            logger.error(
                    "Exception in {}.{}(): {}",
                    origin.getClassName(),
                    origin.getMethodName(),
                    e.getMessage(),
                    e
            );
        } else {
            logger.error("Unhandled exception: {}", e.getMessage(), e);
        }
    }
}
