package smalldomains.domainmanager.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import smalldomains.domainmanager.exception.NoSmallDomainExists;
import smalldomains.domainmanager.exception.SmallDomainAlreadyExists;

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

    @ExceptionHandler(SmallDomainAlreadyExists.class)
    public ResponseEntity<Map<String, String>> handleSmallDomainAlreadyExists(final SmallDomainAlreadyExists ex) {
        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(generateErrorBody(String.format("small domain already exists: %s", ex.getAlreadyExistingDomain())));
    }

    @ExceptionHandler(NoSmallDomainExists.class)
    public ResponseEntity<Map<String, String>> handleNoSmallDomainExists(final NoSmallDomainExists ex) {
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(generateErrorBody(ex.getNameOfNonExistentDomain() + " not found"));
    }

    private Map<String, String> generateErrorBody(final String errorMessage) {
        return Map.of(
                "timestamp", LocalDateTime.now().toString(),
                "error", errorMessage
        );
    }

}
