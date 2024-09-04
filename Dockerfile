# gradle:jdk17이라는 기본 이미지를 사용하여 작업을 시작합니다.
FROM gradle:jdk17

# /app 디렉토리를 작업할 공간으로 설정합니다.
WORKDIR /app

# 현재 디렉토리의 모든 파일을 /app으로 복사합니다.
COPY . .

# Gradle 빌드 파일에 실행 권한을 부여하고, 프로젝트를 빌드합니다.
RUN chmod +x gradlew

RUN ./gradlew clean build

# 8080번 포트를 외부에 열어줍니다.
EXPOSE 8080

# 애플리케이션이 실행될 때 사용할 환경 변수들을 설정합니다.
ENV DB_URL=${DB_URL}
ENV DB_USERNAME=${DB_USERNAME}
ENV DB_PASSWORD=${DB_PASSWORD}
ENV OPENAI_API_KEY=${OPENAI_API_KEY}
ENV AIR_PUBLIC_API_KEY=${AIR_PUBLIC_API_KEY}
ENV WEATHER_PUBLIC_API_KEY=${WEATHER_PUBLIC_API_KEY}
ENV SENTRY_DSN=${SENTRY_DSN}
ENV SENTRY_AUTH_TOKEN=${SENTRY_AUTH_TOKEN}
ENV SPRING_SECURITY_OAUTH2_CLIENT_REGISTRATION_KAKAO_CLIENT_ID=${SPRING_SECURITY_OAUTH2_CLIENT_REGISTRATION_KAKAO_CLIENT_ID}
ENV JWT_SECRET=${JWT_SECRET}

# 설정된 명령어를 통해 애플리케이션을 실행합니다.
CMD ["java", "-jar", "build/libs/sumda-0.0.1-SNAPSHOT.jar"]

# 도커 컨테이너 시작할 때 docker run -p 8080:8080 --env-file .env backend 입력하기
