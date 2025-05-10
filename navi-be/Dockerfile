# 1단계: 빌드 이미지
FROM gradle:8.5.0-jdk17 AS builder
WORKDIR /app

# 필요한 파일만 복사 (gradle.properties 생략)
COPY build.gradle settings.gradle ./
COPY gradle ./gradle
COPY gradlew gradlew
COPY gradlew.bat gradlew.bat
RUN chmod +x gradlew

# 의존성 캐싱용 빈 빌드
RUN ./gradlew build -x test --no-daemon || true

# 전체 프로젝트 복사
COPY . .

# 실제 빌드
RUN ./gradlew build -x test --no-daemon --stacktrace

# 2단계: 실행용 이미지
FROM openjdk:17-jdk-slim
WORKDIR /app

COPY --from=builder /app/build/libs/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
