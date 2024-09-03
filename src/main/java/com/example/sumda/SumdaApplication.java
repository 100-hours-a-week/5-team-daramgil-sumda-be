package com.example.sumda;

import io.sentry.Sentry;
import io.sentry.spring.jakarta.EnableSentry;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import io.github.cdimascio.dotenv.Dotenv;

@SpringBootApplication
public class SumdaApplication {

	public static void main(String[] args) {

		SpringApplication.run(SumdaApplication.class, args);

		// Sentry 테스트 코드
		try {
			throw new Exception("This is a test exception for Sentry");
		} catch (Exception e) {
			Sentry.captureException(e);
		}
	}
}
