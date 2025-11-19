# 1. 자바 17이 설치된 베이스 이미지 사용
FROM eclipse-temurin:17-jdk-jammy

# 2. 컨테이너 안에서 작업할 디렉터리
WORKDIR /app

# 3. 로컬에서 빌드된 JAR를 컨테이너로 복사
#   - build/libs 안에 jar가 하나뿐이라는 가정 하에 *로 복사
COPY build/libs/*.jar app.jar

# 4. 컨테이너가 노출할 포트 (Spring Boot 기본 8080)
EXPOSE 8080

# 5. 컨테이너가 시작될 때 실행할 명령
ENTRYPOINT ["java", "-jar", "/app/app.jar"]