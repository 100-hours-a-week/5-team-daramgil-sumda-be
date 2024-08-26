package com.example.sumda.config;

import io.sentry.Sentry;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
public class SentryConfig {

    @Value("${sentry.dsn}")
    private String sentryDsn;

    @PostConstruct
    public void init() {
        Sentry.init(options -> options.setDsn(sentryDsn));
    }

}
