# Используем подходящий образ Maven с Java 21
FROM maven:3.9.4-eclipse-temurin-21 AS builder

# Устанавливаем рабочую директорию внутри контейнера
WORKDIR /app

# Копируем POM и исходный код в контейнер
COPY pom.xml .
COPY src ./src

# Сборка проекта с указанным профилем
RUN mvn clean package -Pproduction -DskipTests

# Используем образ только с JDK для финального запуска
FROM eclipse-temurin:21-jdk-jammy

# Устанавливаем рабочую директорию
WORKDIR /app

# Копируем собранный JAR файл из предыдущего этапа
COPY --from=builder /app/target/*.jar app.jar

# Указываем команду для запуска приложения
ENTRYPOINT ["java", "-jar", "app.jar"]
