package smalldomains.domainmanager.health;

import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.ReactiveHealthIndicator;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import smalldomains.domainmanager.repository.SmallDomainRepository;

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
                .map(Health.Builder::build);
    }

}
