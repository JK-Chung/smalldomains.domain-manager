package smalldomains.domainmanager.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import smalldomains.domainmanager.exception.SmallDomainAlreadyExistsException;

import java.time.LocalDateTime;
import java.util.Map;

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
