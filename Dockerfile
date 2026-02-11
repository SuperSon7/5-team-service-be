########################################
# Stage 1: Build
########################################
FROM eclipse-temurin:21.0.5_11-jdk-alpine AS build

WORKDIR /workspace

# Gradle wrapper + 빌드 설정 파일 (루트 + 서브모듈) → 의존성 캐싱
COPY gradlew .
COPY gradle gradle
COPY build.gradle settings.gradle ./
COPY api/build.gradle api/
COPY chat/build.gradle chat/
COPY common/build.gradle common/

RUN chmod +x gradlew \
    && ./gradlew dependencies --no-daemon --stacktrace

# 소스 복사 후 빌드 (테스트 제외)
COPY api/src api/src
COPY chat/src chat/src
COPY common/src common/src
RUN ./gradlew :api:bootJar --no-daemon -x test

########################################
# Stage 2: JAR 레이어 추출
########################################
FROM eclipse-temurin:21.0.5_11-jdk-alpine AS extract

WORKDIR /workspace
COPY --from=build /workspace/api/build/libs/doktori-api.jar application.jar
RUN java -Djarmode=tools -jar application.jar extract --layers --launcher --destination extracted

########################################
# Stage 3: Runtime
########################################
FROM eclipse-temurin:21.0.5_11-jre-alpine AS runtime

WORKDIR /app

# 보안: non-root 사용자로 실행
RUN addgroup -S app && adduser -S app -G app

# 레이어별 COPY — 변경 빈도 낮은 순서대로 (Docker 캐시 최적화)
COPY --from=extract /workspace/extracted/dependencies/ ./
COPY --from=extract /workspace/extracted/spring-boot-loader/ ./
COPY --from=extract /workspace/extracted/snapshot-dependencies/ ./
COPY --from=extract /workspace/extracted/application/ ./

USER app

EXPOSE 8080

ENTRYPOINT ["java", "org.springframework.boot.loader.launch.JarLauncher"]
