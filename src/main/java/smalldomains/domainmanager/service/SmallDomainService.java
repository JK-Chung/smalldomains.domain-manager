package smalldomains.domainmanager.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import smalldomains.domainmanager.entity.SmallDomainEntity;
import smalldomains.domainmanager.exception.NoSmallDomainExists;
import smalldomains.domainmanager.mapper.SmallDomainMapper;
import smalldomains.domainmanager.repository.SmallDomainRepository;
import smalldomains.domainmanager.restDto.CreateRandomSmallDomainRequest;
import smalldomains.domainmanager.restDto.SmallDomainDto;
import smalldomains.domainmanager.utility.RandomSmallDomainGenerator;

import java.time.Instant;
import java.time.Period;

/**
 * Service for the management of small domains
 */
@Slf4j
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
        final SmallDomainEntity toSave = createRequestToSmallDomainEntity(createRequest);
        return Mono.fromFuture(repository.saveSmallDomain(toSave))
                .map(SmallDomainMapper::entityToDto);
    }

    public Mono<SmallDomainDto> getSmallDomain(final String smallDomain) throws NoSmallDomainExists {
        return Mono.fromFuture(repository.getSmallDomain(smallDomain))
                .flatMap(optRetrievedSmallDomain -> optRetrievedSmallDomain
                                .map(SmallDomainMapper::entityToDto)
                                .map(Mono::just)
                                .orElse(Mono.error(new NoSmallDomainExists(smallDomain)))
                )
                .flatMap(retrievedSmallDomain -> {
                    final boolean hasSmallDomainExpired = Instant.now().isAfter(Instant.ofEpochSecond(retrievedSmallDomain.expiringAt()));
                    if(hasSmallDomainExpired) {
                        repository.deleteSmallDomain(smallDomain).thenAccept(ignorable -> log.error("Retrieved an expired SmallDomain. It has now been deleted. {}", retrievedSmallDomain));
                        return Mono.error(new NoSmallDomainExists(smallDomain));
                    } else {
                        return Mono.just(retrievedSmallDomain);
                    }
                });
    }

    private SmallDomainEntity createRequestToSmallDomainEntity(final CreateRandomSmallDomainRequest createRequest) {
        return new SmallDomainEntity(
                randomSmallDomainGenerator.generateRandomSmallDomain(),
                getAbsoluteUrl(createRequest.largeDomain()),
                Instant.now().getEpochSecond(),
                Instant.now().plus(Period.ofDays(DAILY_DURATION_OF_ANON_SMALL_DOMAINS)).getEpochSecond()
        );
    }

    private String getAbsoluteUrl(final String largeDomain) {
        if(largeDomain.startsWith("https://") || largeDomain.startsWith("http://")) {
            return largeDomain;
        } else {
            return "https://" + largeDomain;
        }
    }

}
