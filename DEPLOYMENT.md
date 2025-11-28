# Docker 클라우드 배포 가이드

## 📦 배포 개요

로컬에서 Docker 이미지를 빌드하여 Docker Hub에 업로드하고, 클라우드 서버에서 이미지를 pull 받아 실행하는 방식입니다.

---

## 🚀 배포 순서

### 1단계: 로컬에서 JAR 파일 빌드
```bash
# JAR 파일 생성
./gradlew clean bootJar

# 빌드 결과 확인
ls -lh build/libs/weather-0.0.1-SNAPSHOT.jar
```

### 2단계: Docker 이미지 빌드
```bash
# Docker 이미지 빌드 (your-dockerhub-username을 본인 계정으로 변경)
sudo docker build -t your-dockerhub-username/weather-app:latest .

# 이미지 확인
sudo docker images | grep weather-app
```

### 3단계: Docker Hub에 푸시
```bash
# Docker Hub 로그인
sudo docker login

# 이미지 푸시
sudo docker push your-dockerhub-username/weather-app:latest

# (선택) 버전 태그 추가
sudo docker tag your-dockerhub-username/weather-app:latest your-dockerhub-username/weather-app:v1.0.0
sudo docker push your-dockerhub-username/weather-app:v1.0.0
```

### 4단계: 클라우드 서버에서 docker-compose.yml 작성

클라우드 서버에 접속하여 다음 내용으로 `docker-compose.yml` 파일을 생성하세요.

```bash
# 클라우드 서버 접속 (예시)
ssh user@your-server-ip

# 작업 디렉터리 생성
mkdir -p ~/weather-app
cd ~/weather-app

# docker-compose.yml 생성
vi docker-compose.yml
```

**docker-compose.yml 내용:**
```yaml
version: '3.8'

services:
  mysql:
    image: mysql:8.0
    container_name: mysql
    ports:
      - "3306:3306"
    environment:
      - "MYSQL_ROOT_PASSWORD=your-password-here"
      - "MYSQL_DATABASE=weather_db"
      - "TZ=Asia/Seoul"
      - "LC_ALL=C.UTF-8"
    command:
      - --character-set-server=utf8mb4
      - --collation-server=utf8mb4_unicode_ci
    volumes:
      - mysql_data:/var/lib/mysql
    healthcheck:
      test: ["CMD", "mysqladmin", "ping", "-h", "localhost", "-u", "root", "-pyour-password-here"]
      interval: 10s
      timeout: 5s
      retries: 5
      start_period: 30s

  weather-app:
    image: your-dockerhub-username/weather-app:latest
    container_name: weather-app
    ports:
      - "8080:8080"
    environment:
      - "SPRING_PROFILES_ACTIVE=prod"
      - "DB_HOST=mysql"
      - "DB_PORT=3306"
      - "DB_NAME=weather_db"
      - "DB_USERNAME=root"
      - "DB_PASSWORD=your-password-here"
      - "SERVER_PORT=8080"
    depends_on:
      mysql:
        condition: service_healthy
    volumes:
      - weather_tmp:/tmp

volumes:
  mysql_data:
  weather_tmp:
```

**변경 필요:**
- `your-dockerhub-username`: 본인의 Docker Hub 사용자명
- `your-password-here`: MySQL 비밀번호 (총 3곳)

### 5단계: 클라우드 서버에서 실행
```bash
# Docker Compose로 컨테이너 실행
sudo docker compose up -d

# 컨테이너 상태 확인
sudo docker compose ps

# 로그 확인
sudo docker compose logs -f weather-app
```

### 6단계: 배포 확인
```bash
# 서버 상태 확인
curl http://localhost:8080/api/health

# DB 연결 확인
curl http://localhost:8080/api/health/db

# 외부에서 접속 (your-server-ip를 실제 IP로 변경)
curl http://your-server-ip:8080/api/health
```

---

## 🔄 재배포 (코드 변경 시)

### 로컬에서 작업
```bash
# 1. 코드 수정 후 커밋
git add .
git commit -m "Update feature"
git push origin develop

# 2. JAR 재빌드
./gradlew clean bootJar

# 3. Docker 이미지 재빌드
sudo docker build -t your-dockerhub-username/weather-app:latest .

# 4. Docker Hub에 푸시
sudo docker push your-dockerhub-username/weather-app:latest
```

### 클라우드 서버에서 작업
```bash
# 1. 최신 이미지 pull
sudo docker compose pull weather-app

# 2. 컨테이너 재시작
sudo docker compose up -d

# 3. 로그 확인
sudo docker compose logs -f weather-app

# 또는 한 번에
sudo docker compose pull && sudo docker compose up -d
```

---

## 📝 주요 명령어

### 컨테이너 관리
```bash
# 시작
sudo docker compose up -d

# 중지
sudo docker compose stop

# 재시작
sudo docker compose restart

# 중지 및 삭제
sudo docker compose down

# 중지 + 볼륨 삭제 (데이터 초기화)
sudo docker compose down -v
```

### 로그 확인
```bash
# 전체 로그
sudo docker compose logs

# 실시간 로그
sudo docker compose logs -f

# 특정 서비스 로그
sudo docker compose logs -f weather-app
sudo docker compose logs -f mysql

# 최근 100줄
sudo docker compose logs --tail=100 weather-app
```

### 데이터베이스 관리
```bash
# MySQL 접속
sudo docker exec -it mysql mysql -uroot -p

# 백업
sudo docker exec mysql mysqldump -uroot -p weather_db > backup_$(date +%Y%m%d).sql

# 복원
sudo docker exec -i mysql mysql -uroot -p weather_db < backup_20251115.sql
```

---

## 📊 상태 모니터링

```bash
# 컨테이너 상태
sudo docker compose ps

# 리소스 사용량
sudo docker stats

# 디스크 사용량
sudo docker system df
```

---

## 🌐 접속 정보

- **API 서버**: http://localhost:8080
- **Health Check**: http://localhost:8080/api/health
- **MySQL**: localhost:3306
  - Database: `weather_db`
  - Username: `root`
  - Password: `docker-compose.yml`에서 설정
