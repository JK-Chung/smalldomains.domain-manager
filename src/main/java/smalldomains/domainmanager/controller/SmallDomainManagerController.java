package smalldomains.domainmanager.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;
import smalldomains.domainmanager.entity.SmallDomain;
import smalldomains.domainmanager.service.SmallDomainService;

import javax.validation.Valid;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

/**
 * Controller handles the management of small domains.
 * Responsible for:
 *  * Calling the appropriate services
 *  * Specifying the REST API contract
 *  * Validating user input
 *
 *  NOT responsible for:
 *   * Exception handling and error handling (that is done by the global ControllerAdvice)
 */
@Slf4j
@Validated
@RestController
@RequestMapping("/smalldomains")
@RequiredArgsConstructor
public class SmallDomainManagerController {
    private final SmallDomainService smallDomainService;

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping(consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
    public Mono<SmallDomain> createSmallDomain(@Valid @RequestBody final SmallDomain newSmallDomain, final ServerHttpRequest request) {
        log.info("Received a %s request (%s) at %s".formatted(request.getMethodValue(), request.getId(), request.getPath()));
        return smallDomainService.createNewSmallDomain(newSmallDomain);
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping(path = "{smallDomain}", produces = APPLICATION_JSON_VALUE)
    public Mono<SmallDomain> getSmallDomain(@PathVariable final String smallDomain, final ServerHttpRequest request) {
        log.info("Received a %s request (%s) at %s".formatted(request.getMethodValue(), request.getId(), request.getPath()));
        return smallDomainService.getSmallDomain(smallDomain);
    }

}
