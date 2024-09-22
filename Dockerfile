FROM gradle:jdk17

WORKDIR /app

ARG JAR_FILE=build/libs/sumda-0.0.1-SNAPSHOT.jar

COPY ${JAR_FILE} sumda-0.0.1-SNAPSHOT.jar

ENV TZ=Asia/Seoul

EXPOSE 8080

CMD ["java", "-jar", "sumda-0.0.1-SNAPSHOT.jar"]

# 도커 컨테이너 시작할 때 docker run -p 8080:8080 --env-file .env 이미지이름 입력하기