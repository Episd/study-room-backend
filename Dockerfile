# 1️⃣ 使用稳定的 JDK 17（Spring Boot 推荐）
FROM eclipse-temurin:17-jre

# 2️⃣ 设置容器内工作目录
WORKDIR /app

# 3️⃣ 拷贝构建好的 jar
COPY target/study-room-backend-0.0.1-SNAPSHOT.jar app.jar

# 4️⃣ 声明服务端口（说明用，不等于暴露）
EXPOSE 3000

# 5️⃣ 启动 Spring Boot
ENTRYPOINT ["java","-jar","/app/app.jar"]
