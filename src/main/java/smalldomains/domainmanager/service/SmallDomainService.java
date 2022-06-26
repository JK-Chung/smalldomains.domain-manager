package smalldomains.domainmanager.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import smalldomains.domainmanager.entity.SmallDomainEntity;
import smalldomains.domainmanager.mapper.SmallDomainMapper;
import smalldomains.domainmanager.restDto.CreateRandomSmallDomainRequest;
import smalldomains.domainmanager.restDto.SmallDomainDto;
import smalldomains.domainmanager.exception.NoSmallDomainExists;
import smalldomains.domainmanager.repository.SmallDomainRepository;
import smalldomains.domainmanager.utility.RandomSmallDomainGenerator;

import java.time.Instant;
import java.time.Period;

/**
 * Service for the management of small domains
 */
@Service
public class SmallDomainService {

    private final RandomSmallDomainGenerator randomSmallDomainGenerator;
    private final SmallDomainRepository repository;

    private final int DAILY_DURATION_OF_ANON_SMALL_DOMAINS;

    public SmallDomainService(
            RandomSmallDomainGenerator randomSmallDomainGenerator,
            SmallDomainRepository repository,
            @Value("${smalldomains.dailyDurationOfAnonSmallDomains}") int dailyDurationOfAnonSmallDomains
    ) {
        this.randomSmallDomainGenerator = randomSmallDomainGenerator;
        this.repository = repository;
        this.DAILY_DURATION_OF_ANON_SMALL_DOMAINS = dailyDurationOfAnonSmallDomains;
    }


    public Mono<SmallDomainDto> createRandomSmallDomain(final CreateRandomSmallDomainRequest createRequest) {
        final SmallDomainEntity toSave = new SmallDomainEntity(
                randomSmallDomainGenerator.generateRandomSmallDomain(),
                createRequest.largeDomain(),
                Instant.now().getEpochSecond(),
                Instant.now().plus(Period.ofDays(DAILY_DURATION_OF_ANON_SMALL_DOMAINS)).getEpochSecond()
        );

        return Mono.fromFuture(repository.saveSmallDomain(toSave))
                .map(SmallDomainMapper::entityToDto);
    }

    public Mono<SmallDomainDto> getSmallDomain(final String smallDomain) throws NoSmallDomainExists {
        return Mono.fromFuture(repository.getSmallDomain(smallDomain))
                .flatMap(optRetrievedSmallDomain -> optRetrievedSmallDomain
                                .map(SmallDomainMapper::entityToDto)
                                .map(Mono::just)
                                .orElseGet(() -> Mono.error(new NoSmallDomainExists(smallDomain)))
                );
    }

}
