# 1단계: Gradle 빌드 (빌더 컨테이너)
FROM gradle:8.5.0-jdk17 AS builder
WORKDIR /app

# 캐시를 활용하기 위해 의존성 먼저 복사
COPY build.gradle.kts settings.gradle gradle.properties ./
COPY gradle ./gradle
RUN gradle build --no-daemon || return 0

# 전체 프로젝트 복사 후 빌드
COPY . .
RUN gradle build -x test --no-daemon

# 2단계: 실행 이미지 (Slim한 JDK)
FROM openjdk:17-jdk-slim
WORKDIR /app

# 빌드된 jar 복사
COPY --from=builder /app/build/libs/*.jar app.jar

# 포트 (Render는 자동 인식하지만 명시적으로 적을 수도 있음)
EXPOSE 8080

# 실행
ENTRYPOINT ["java", "-jar", "app.jar"]
