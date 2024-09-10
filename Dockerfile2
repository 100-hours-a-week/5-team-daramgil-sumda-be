FROM gradle:jdk17

WORKDIR /app

COPY . .

ENV TZ=Asia/Seoul

EXPOSE 8080

CMD ["java", "-jar", "build/libs/sumda-0.0.1-SNAPSHOT.jar"]