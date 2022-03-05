package smalldomains.domainmanager.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import smalldomains.domainmanager.exception.SmallDomainAlreadyExistsException;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * This application has been designed to throw the correct error HTTP codes/messages when various different exceptions occur
 * This creates a new ExceptionHandler for each type of exception which may be thrown. Each ExceptionHandler composes the most appropriate
 * HTTP response code and messages.
 *
 * The ControllerAdvice annotation means that these handlers apply globally - to all controllers
 */
@ControllerAdvice
public class GlobalControllerExceptionHandler {

    @ExceptionHandler(SmallDomainAlreadyExistsException.class)
    public ResponseEntity<Map<String, String>> handleApiCallError(final SmallDomainAlreadyExistsException ex) {
        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(Map.of(
                        "timestamp", LocalDateTime.now().toString(),
                        "error", String.format("small domain already exists: %s", ex.getAlreadyExistingDomain())
                ));
    }

}
