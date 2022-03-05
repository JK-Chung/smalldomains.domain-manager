package smalldomains.domainmanager.controller;

import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.support.WebExchangeBindException;
import smalldomains.domainmanager.exception.NoSmallDomainExists;
import smalldomains.domainmanager.exception.SmallDomainAlreadyExists;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * This application has been designed to throw the correct error HTTP codes/messages when various different exceptions occur
 * This creates a new ExceptionHandler for each type of exception which may be thrown. Each ExceptionHandler composes the most appropriate
 * HTTP response code and messages.
 *
 * The ControllerAdvice annotation means that these handlers apply globally - to all controllers
 */
@Log4j2
@ControllerAdvice
public class GlobalControllerExceptionHandler {

    @ResponseBody
    @ResponseStatus(HttpStatus.CONFLICT)
    @ExceptionHandler(SmallDomainAlreadyExists.class)
    public Map<String, String> handleSmallDomainAlreadyExists(final SmallDomainAlreadyExists ex) {
        return generateErrorBody(String.format("small domain already exists: %s", ex.getAlreadyExistingDomain()));
    }

    @ResponseBody
    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(NoSmallDomainExists.class)
    public Map<String, String> handleNoSmallDomainExists(final NoSmallDomainExists ex) {
        return generateErrorBody(ex.getNameOfNonExistentDomain() + " not found");
    }

    @ResponseBody
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(WebExchangeBindException.class)
    public Map<String, Object> handleValidationExceptions(final WebExchangeBindException ex) {
        final Map<String, Object> mutableResponse = new LinkedHashMap<>(
            generateErrorBody("Bad Request - Check \"validationErrors\" for more details")
        );

        mutableResponse.put("validationErrors", generateValidationErrors(ex));

        return mutableResponse;
    }

    private Map<String, String> generateErrorBody(final String errorMessage) {
        return Map.of(
                "timestamp", LocalDateTime.now().toString(),
                "error", errorMessage
        );
    }

    private Map<String, String> generateValidationErrors(final WebExchangeBindException ex) {
        return ex.getBindingResult().getFieldErrors().stream()
                .collect(Collectors.toMap(FieldError::getField, fieldError -> {
                    final String message = fieldError.getDefaultMessage();
                    if(message == null) {
                        log.error("No validation message has been set up for field. Can you set this up to improve UX? {}", fieldError);
                        return "invalid value";
                    } else {
                        return message;
                    }
                }));
    }

}
