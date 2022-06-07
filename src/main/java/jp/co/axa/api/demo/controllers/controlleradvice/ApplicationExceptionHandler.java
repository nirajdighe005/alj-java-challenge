package jp.co.axa.api.demo.controllers.controlleradvice;

import jp.co.axa.api.demo.dto.response.VoidResponseDTO;
import jp.co.axa.api.demo.exceptions.EmployeeAPIException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@ControllerAdvice
public class ApplicationExceptionHandler extends ResponseEntityExceptionHandler {

    private static final String EXCEPTION_PREFIX = "Following Exception Occurred : ";
    private static final String MALFORMED_REQUEST_PREFIX = "Malformed Request : ";
    private static final String INVALID_FIELDS_PREFIX = "Invalid Fields:";

    @ExceptionHandler(EmployeeAPIException.class)
    @Order(Ordered.HIGHEST_PRECEDENCE)
    public ResponseEntity<VoidResponseDTO> employeeAPIException(EmployeeAPIException exception, WebRequest req) {
        logException(exception, req);
        VoidResponseDTO failureResponse = new VoidResponseDTO(EXCEPTION_PREFIX + exception.getMessage());
        return new ResponseEntity<>(failureResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    private void logException(Throwable exception, WebRequest req) {
        log.error("Exception For Request: {}", req.getDescription(false));
        log.error(EXCEPTION_PREFIX, exception);
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex, HttpHeaders headers,
                                                                  HttpStatus status, WebRequest request) {
        logException(ex, request);
        List<FieldError> fieldErrors = ex.getBindingResult().getFieldErrors();
        String invalidFields = fieldErrors.stream().map(FieldError::getField).collect(Collectors.joining(","));
        VoidResponseDTO failureResponse = new VoidResponseDTO(MALFORMED_REQUEST_PREFIX + INVALID_FIELDS_PREFIX + invalidFields);
        return new ResponseEntity<>(failureResponse, status);
    }

    @Override
    protected ResponseEntity<Object> handleHttpMessageNotReadable(HttpMessageNotReadableException ex, HttpHeaders headers, HttpStatus status, WebRequest webRequest) {
        logException(ex, webRequest);
        VoidResponseDTO failureResponse = new VoidResponseDTO(MALFORMED_REQUEST_PREFIX + ex.getMostSpecificCause().getLocalizedMessage());
        return new ResponseEntity<>(failureResponse, status);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<VoidResponseDTO> globalException(AccessDeniedException exception, WebRequest req) {
        logException(exception, req);
        VoidResponseDTO failureResponse = new VoidResponseDTO(EXCEPTION_PREFIX + exception.getLocalizedMessage());
        return new ResponseEntity<>(failureResponse, HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(Throwable.class)
    public ResponseEntity<VoidResponseDTO> globalException(Throwable exception, WebRequest req) {
        logException(exception, req);
        VoidResponseDTO failureResponse = new VoidResponseDTO(EXCEPTION_PREFIX + exception.getLocalizedMessage());
        return new ResponseEntity<>(failureResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
