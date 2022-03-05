package smalldomains.domainmanager.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;
import smalldomains.domainmanager.entity.SmallDomain;
import smalldomains.domainmanager.service.SmallDomainService;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
@RequestMapping("smalldomain")
@RequiredArgsConstructor
public class SmallDomainManagerController {
    private final SmallDomainService smallDomainService;

    @PostMapping(value = "create", consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
    public Mono<SmallDomain> createSmallDomain(@RequestBody final SmallDomain newSmallDomain) {
        return smallDomainService.createNewSmallDomain(newSmallDomain);
    }

}
