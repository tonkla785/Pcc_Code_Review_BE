FROM eclipse-temurin:21-jdk-jammy

RUN apt-get update && apt-get install -y \
    git \
    curl \
    unzip \
 && rm -rf /var/lib/apt/lists/*

# install sonar-scanner
RUN curl -fL -o sonar.zip \
    https://binaries.sonarsource.com/Distribution/sonar-scanner-cli/sonar-scanner-cli-5.0.1.3006-linux.zip \
 && unzip sonar.zip -d /opt \
 && ln -s /opt/sonar-scanner-*/bin/sonar-scanner /usr/local/bin/sonar-scanner \
 && rm sonar.zip

WORKDIR /app
COPY target/*.jar app.jar

EXPOSE 8080
ENTRYPOINT ["java","-jar","app.jar"]
