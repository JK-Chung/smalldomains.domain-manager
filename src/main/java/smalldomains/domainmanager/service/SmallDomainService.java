package smalldomains.domainmanager.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import smalldomains.domainmanager.entity.SmallDomain;
import smalldomains.domainmanager.exception.NoSmallDomainExists;
import smalldomains.domainmanager.exception.SmallDomainAlreadyExists;
import smalldomains.domainmanager.repository.SmallDomainRepository;

/**
 * Service for the management of small domains
 */
@Service
@RequiredArgsConstructor
public class SmallDomainService {
    private final SmallDomainRepository repository;

    public Mono<SmallDomain> createNewSmallDomain(final SmallDomain toSave) throws SmallDomainAlreadyExists {
        return Mono.fromFuture(repository.getSmallDomain(toSave.getSmallDomain()))
                .flatMap(optRetrievedSmallDomain -> {
                    if(optRetrievedSmallDomain.isPresent()) {
                        // small domain already exists - don't make another one
                        return Mono.error(new SmallDomainAlreadyExists(toSave.getSmallDomain()));
                    } else {
                        // no existing small domain found - we can proceed with its creation
                        return Mono.fromFuture(repository.saveSmallDomain(toSave));
                    }
                });
    }

    public Mono<SmallDomain> getSmallDomain(final String smallDomain) throws NoSmallDomainExists {
        return Mono.fromFuture(repository.getSmallDomain(smallDomain))
                .flatMap(optRetrievedSmallDomain -> optRetrievedSmallDomain
                                .map(Mono::just)
                                .orElseGet(() -> Mono.error(new NoSmallDomainExists(smallDomain)))
                );
    }
}
