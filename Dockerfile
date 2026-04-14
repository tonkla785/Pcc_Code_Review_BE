FROM eclipse-temurin:21-jdk-jammy

# ===== system tools =====
RUN apt-get update && apt-get install -y \
    git \
    curl \
    unzip \
    bash \
    ca-certificates \
 && rm -rf /var/lib/apt/lists/*

# ===== install Node.js 18 =====
RUN apt-get update && apt-get install -y ca-certificates curl gnupg \
 && curl -fsSL https://deb.nodesource.com/setup_18.x | bash - \
 && apt-get install -y nodejs \
 && node -v \
 && npm -v \
 && rm -rf /var/lib/apt/lists/*

# ===== install Maven =====
RUN apt-get update && apt-get install -y maven \
 && mvn -v \
 && rm -rf /var/lib/apt/lists/*

# ===== install Gradle =====
RUN curl -fsSL https://services.gradle.org/distributions/gradle-8.6-bin.zip -o gradle.zip \
 && unzip gradle.zip -d /opt \
 && ln -s /opt/gradle-*/bin/gradle /usr/local/bin/gradle \
 && gradle -v \
 && rm gradle.zip

# ===== install sonar-scanner =====
RUN curl -fL -o sonar.zip \
    https://binaries.sonarsource.com/Distribution/sonar-scanner-cli/sonar-scanner-cli-5.0.1.3006-linux.zip \
 && unzip sonar.zip -d /opt \
 && ln -s /opt/sonar-scanner-*/bin/sonar-scanner /usr/local/bin/sonar-scanner \
 && sonar-scanner -v \
 && rm sonar.zip

# ===== install JDK 8 (สำหรับ project Java 8) =====
RUN apt-get update && apt-get install -y wget apt-transport-https gnupg \
 && wget -qO - https://packages.adoptium.net/artifactory/api/gpg/key/public | gpg --dearmor -o /etc/apt/trusted.gpg.d/adoptium.gpg \
 && echo "deb https://packages.adoptium.net/artifactory/deb jammy main" > /etc/apt/sources.list.d/adoptium.list \
 && apt-get update && apt-get install -y temurin-8-jdk \
 && rm -rf /var/lib/apt/lists/*

# ===== install ojdbc6 เข้า local Maven repo (ทั้ง 2 groupId) =====
COPY libs/ojdbc6.jar /tmp/ojdbc6.jar

RUN mvn install:install-file \
    -Dfile=/tmp/ojdbc6.jar \
    -DgroupId=com.oracle.database.jdbc \
    -DartifactId=ojdbc6 \
    -Dversion=11.2.0.4 \
    -Dpackaging=jar \
    -DgeneratePom=true

RUN mvn install:install-file \
    -Dfile=/tmp/ojdbc6.jar \
    -DgroupId=com.oracle \
    -DartifactId=ojdbc6 \
    -Dversion=11.2.0.4 \
    -Dpackaging=jar \
    -DgeneratePom=true

RUN mvn install:install-file \
    -Dfile=/tmp/ojdbc6.jar \
    -DgroupId=com.oracle \
    -DartifactId=ojdbc6 \
    -Dversion=11.2.0 \
    -Dpackaging=jar \
    -DgeneratePom=true

RUN rm /tmp/ojdbc6.jar

# ===== app =====
WORKDIR /app
COPY target/*.jar app.jar

EXPOSE 8080
ENTRYPOINT ["java","-jar","app.jar"]