package smalldomains.domainmanager.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.support.WebExchangeBindException;
import smalldomains.domainmanager.exception.NoSmallDomainExists;
import smalldomains.domainmanager.exception.SmallDomainAlreadyExists;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * This application has been designed to throw the correct error HTTP codes/messages when various different exceptions occur
 * This creates a new ExceptionHandler for each type of exception which may be thrown. Each ExceptionHandler composes the most appropriate
 * HTTP response code and messages.
 *
 * The ControllerAdvice annotation means that these handlers apply globally - to all controllers. It's really neat!
 */
@Slf4j
@ControllerAdvice
public class GlobalControllerExceptionHandler {

    @ResponseBody
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(Exception.class)
    public Map<String, Object> handleAllOtherExceptions(final ServerHttpRequest request, final Exception ex) {
        log.error("Encountered exception when processing requestId {}", request.getId(), ex);
        return generateErrorBody(request, "Internal Server Error");
    }

    @ResponseBody
    @ResponseStatus(HttpStatus.CONFLICT)
    @ExceptionHandler(SmallDomainAlreadyExists.class)
    public Map<String, Object> handleSmallDomainAlreadyExists(final ServerHttpRequest request, final SmallDomainAlreadyExists ex) {
        log.info("RequestId %s tried to create an already-existent SmallDomain (%s)".formatted(request.getId(), ex.getAlreadyExistingDomain()));
        return generateErrorBody(request, "This SmallDomain already exists: %s".formatted(ex.getAlreadyExistingDomain()));
    }

    @ResponseBody
    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(NoSmallDomainExists.class)
    public Map<String, Object> handleNoSmallDomainExists(final ServerHttpRequest request, final NoSmallDomainExists ex) {
        log.info("RequestId %s tried to retrieve a non-existent SmallDomain (%s)".formatted(request.getId(), ex.getNameOfNonExistentDomain()));
        return generateErrorBody(request, "SmallDomain of %s does not exist".formatted(ex.getNameOfNonExistentDomain()));
    }

    @ResponseBody
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(WebExchangeBindException.class)
    public Map<String, Object> handleValidationExceptions(final ServerHttpRequest request, final WebExchangeBindException ex) {
        log.info("RequestId %s made a badly-constructed request.".formatted(request.getId()));

        final Map<String, Object> errorBody = generateErrorBody(
                request,
                "Bad Request - Check \"validationErrors\" for more details"
        );

        errorBody.put("validationErrors", generateValidationErrors(ex));

        return errorBody;
    }

    private Map<String, Object> generateErrorBody(final ServerHttpRequest request, final String errorMessage) {
        // would usually use Map.of but I want to guarantee the Map's implementation to ensure Map entries occur in the order they were inserted
        final var errorBody = new LinkedHashMap<String, Object>();
        
        errorBody.put("timestamp", LocalDateTime.now().toString());
        errorBody.put("requestId", request.getId());
        errorBody.put("path", request.getPath().value());
        errorBody.put("method", request.getMethodValue());
        errorBody.put("error", errorMessage);
        
        return errorBody;
    }

    private Map<String, ?> generateValidationErrors(final WebExchangeBindException ex) {
        final Function<FieldError, String> toUserFriendlyErrorMessage = fieldError -> Optional.ofNullable(fieldError.getDefaultMessage())
                .orElseGet(() -> {
                    log.error("No validation message has been set up for field. Can you set this up to improve UX? {}", fieldError);
                    return "invalid value";
                });

        return ex.getBindingResult().getFieldErrors().stream()
                .collect(Collectors.groupingBy(FieldError::getField, Collectors.mapping(toUserFriendlyErrorMessage, Collectors.toList())));
    }

}
