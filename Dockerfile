FROM eclipse-temurin:21-jdk-jammy

# ===== system tools =====
RUN apt-get update && apt-get install -y \
    git \
    curl \
    unzip \
    bash \
 && rm -rf /var/lib/apt/lists/*

# ===== install Gradle (fallback if no gradlew) =====
RUN curl -fsSL https://services.gradle.org/distributions/gradle-8.6-bin.zip -o gradle.zip \
 && unzip gradle.zip -d /opt \
 && ln -s /opt/gradle-*/bin/gradle /usr/local/bin/gradle \
 && rm gradle.zip

# ===== install sonar-scanner =====
RUN curl -fL -o sonar.zip \
    https://binaries.sonarsource.com/Distribution/sonar-scanner-cli/sonar-scanner-cli-5.0.1.3006-linux.zip \
 && unzip sonar.zip -d /opt \
 && ln -s /opt/sonar-scanner-*/bin/sonar-scanner /usr/local/bin/sonar-scanner \
 && rm sonar.zip

WORKDIR /app
COPY target/*.jar app.jar

EXPOSE 8080
ENTRYPOINT ["java","-jar","app.jar"]