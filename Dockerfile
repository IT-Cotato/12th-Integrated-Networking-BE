# Jar 파일 빌드
FROM gradle:jdk17 AS builder

WORKDIR /app

COPY --chown=gradle:gradle . /app

RUN chmod +x gradlew
RUN ./gradlew bootJar --no-daemon

# 애플리케이션 실행 단계
FROM openjdk:17-jdk-slim

ENV TZ=Asia/Seoul

# 빌드 단계에서 생성된 Jar 파일을 최종 이미지로 복사
COPY --from=builder /app/build/libs/*.jar /app/app.jar

# Spring Boot 애플리케이션 포트
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app/app.jar"]