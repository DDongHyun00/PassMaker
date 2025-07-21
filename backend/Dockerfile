# 경량화된 Java 17 JRE Alpine 이미지 사용
FROM eclipse-temurin:17-jre-alpine

# 빌드된 JAR 파일을 컨테이너 내부로 복사
COPY ./build/libs/backend-0.0.1-SNAPSHOT.jar app.jar

# 컨테이너 실행 시 JAR 실행
ENTRYPOINT ["java", "-jar", "app.jar"]
