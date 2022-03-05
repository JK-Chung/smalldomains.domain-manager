package smalldomains.domainmanager.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import smalldomains.domainmanager.entity.SmallDomain;
import smalldomains.domainmanager.exception.SmallDomainAlreadyExistsException;
import smalldomains.domainmanager.repository.SmallDomainRepository;

/**
 * Service for the management of small domains
 */
@Service
@RequiredArgsConstructor
public class SmallDomainService {
    private final SmallDomainRepository repository;

    public Mono<SmallDomain> createNewSmallDomain(final SmallDomain toSave) throws SmallDomainAlreadyExistsException {
        return Mono.fromFuture(repository.getSmallDomain(toSave.getSmallDomain()))
                .flatMap(optRetrievedSmallDomain -> {
                    if(optRetrievedSmallDomain.isPresent()) {
                        // small domain already exists - don't make another one
                        return Mono.error(new SmallDomainAlreadyExistsException(toSave.getSmallDomain()));
                    } else {
                        // no existing small domain found - we can proceed with its creation
                        return Mono.fromFuture(repository.saveSmallDomain(toSave));
                    }
                });
    }
}
