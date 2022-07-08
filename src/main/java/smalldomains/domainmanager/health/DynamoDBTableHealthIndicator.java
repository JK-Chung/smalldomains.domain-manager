package smalldomains.domainmanager.health;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.ReactiveHealthIndicator;
import org.springframework.boot.actuate.health.Status;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import smalldomains.domainmanager.repository.SmallDomainRepository;

@Slf4j
@Component
public class DynamoDBTableHealthIndicator implements ReactiveHealthIndicator {

    private final SmallDomainRepository repository;

    public DynamoDBTableHealthIndicator(SmallDomainRepository repository) {
        this.repository = repository;
    }

    @Override
    public Mono<Health> health() {
        return Mono.fromFuture(repository.isTableStillAvailable())
                .map(isTableStillAvailable -> isTableStillAvailable ? Health.up() : Health.down())
                .map(Health.Builder::build)
                .doOnSuccess(health -> {
                    if(health.getStatus().equals(Status.UP)) {
                        log.info("Domain-Manager is Healthy: " + health);
                    } else {
                        log.error("Domain-Manager is Unhealthy: " +  health);
                    }
                })
                .doOnError(error -> log.error("Domain-Manager is unhealthy with error: ", error));
    }

}
