package smalldomains.domainmanager.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;
import smalldomains.domainmanager.entity.SmallDomain;
import smalldomains.domainmanager.service.SmallDomainService;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

/**
 * Controller handles the management of small domains
 */
@RestController
@RequestMapping("smalldomain")
@RequiredArgsConstructor
public class SmallDomainManagerController {
    private final SmallDomainService smallDomainService;

    @PostMapping(consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
    public Mono<SmallDomain> createSmallDomain(@RequestBody final SmallDomain newSmallDomain) {
        return smallDomainService.createNewSmallDomain(newSmallDomain);
    }

    @GetMapping(path = "{smallDomain}", produces = APPLICATION_JSON_VALUE)
    public Mono<SmallDomain> getSmallDomain(@PathVariable final String smallDomain) {
        return smallDomainService.getSmallDomain(smallDomain);
    }

}
