package hangman.infrastructure.config;

import hangman.core.Game;
import hangman.util.AccessMonitor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Configuration
public class AccessMonitorConfiguration {

    @Value("${access.monitor.concurrency-level}")
    private Integer concurrencyLevel;

    @Value("${access.monitor.time-in-seconds}")
    private Integer timeInSeconds;


    @Bean(name="mainMonitor")
    public AccessMonitor<String, Optional<Game>> getMainMonitor() {
        return new AccessMonitor<>(concurrencyLevel, timeInSeconds, TimeUnit.SECONDS);
    }
}

