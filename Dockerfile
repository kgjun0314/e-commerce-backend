# 베이스 이미지 (JDK 17 사용)
FROM eclipse-temurin:17-jdk

# 작업 디렉토리 생성
WORKDIR /app

# 빌드된 jar 파일 복사
COPY build/libs/app.jar app.jar

# 시간대 설정
ENV TZ=Asia/Seoul

# 애플리케이션 실행
ENTRYPOINT ["java", "-jar", "app.jar"]