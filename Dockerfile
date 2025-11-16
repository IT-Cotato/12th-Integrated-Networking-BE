FROM eclipse-temurin:17-jdk

# 앱 작업 디렉터리
WORKDIR /app

# Spring Boot가 /tmp를 쓰기 때문에 볼륨으로 분리
VOLUME /tmp

# 호스트에서 빌드한 JAR를 이미지에 복사 (이름 바뀌어도 *.jar로 대응)
ARG JAR_FILE=build/libs/*.jar
COPY ${JAR_FILE} app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-Djava.security.egd=file:/dev/./urandom", "-jar", "app.jar"]