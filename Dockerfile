FROM gradle:jdk17

# Set the working directory inside the container
WORKDIR /app

# Copy the Gradle wrapper and build files
COPY . .

COPY src /app/src

# Grant execute permissions to the Gradle wrapper
RUN chmod +x gradlew

RUN ./gradlew clean build

EXPOSE 8080

# Environment variables setup (these would be replaced by actual values or could be passed in at runtime)
ENV DB_URL=${DB_URL}
ENV DB_USERNAME=${DB_USERNAME}
ENV DB_PASSWORD=${DB_PASSWORD}
ENV OPENAI_API_KEY=${OPENAI_API_KEY}
ENV AIR_PUBLIC_API_KEY=${AIR_PUBLIC_API_KEY}
ENV WEATHER_PUBLIC_API_KEY=${WEATHER_PUBLIC_API_KEY}
ENV WEATHER_PUBLIC_API_KEY2=${WEATHER_PUBLIC_API_KEY2}
ENV SENTRY_DSN=${SENTRY_DSN}

# Command to run the application
CMD ["java", "-jar", "build/libs/sumda-0.0.1-SNAPSHOT.jar"]