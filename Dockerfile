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

# ===== install Google Chrome (for Angular test coverage) =====
RUN curl -fsSL https://dl.google.com/linux/linux_signing_key.pub | gpg --dearmor -o /usr/share/keyrings/google-chrome.gpg \
 && echo "deb [arch=amd64 signed-by=/usr/share/keyrings/google-chrome.gpg] http://dl.google.com/linux/chrome/deb/ stable main" > /etc/apt/sources.list.d/google-chrome.list \
 && apt-get update \
 && apt-get install -y google-chrome-stable \
 && rm -rf /var/lib/apt/lists/* \
 && printf '#!/bin/bash\nexec /usr/bin/google-chrome-stable --no-sandbox --disable-gpu "$@"\n' > /usr/local/bin/chrome-nosandbox \
 && chmod +x /usr/local/bin/chrome-nosandbox
ENV CHROME_BIN=/usr/local/bin/chrome-nosandbox

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

# ===== install JDK 25 (สำหรับ project Java 25) =====
RUN apt-get update && apt-get install -y temurin-25-jdk \
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

# ===== install egp-utils (ทุกเวอร์ชัน) เข้า local Maven repo =====
# วางทั้งโฟลเดอร์ egp-utils จาก .m2 ของ host ไว้ที่ libs/egp-utils/ ใน build context
COPY libs/egp-utils/ /root/.m2/repository/com/pccth/egp-utils/

# ทำความสะอาด + gen .pom ให้ deterministic (จัดการเฉพาะใน image ไม่แตะ source บน host)
RUN set -eu; \
    REPO=/root/.m2/repository/com/pccth/egp-utils; \
    find "$REPO" -type f \( -name '*.lastUpdated' -o -name '_remote.repositories' \
        -o -name 'm2e-lastUpdated.properties' -o -name '*.sha1' \) -delete; \
    for d in "$REPO"/*/; do \
      v=$(basename "$d"); \
      case "$v" in *" "*) echo "drop malformed egp-utils version: [$v]"; rm -rf "$d"; continue;; esac; \
      ls "$d"egp-utils-*.jar >/dev/null 2>&1 || { echo "drop egp-utils version w/o jar: [$v]"; rm -rf "$d"; continue; }; \
    done; \
    for jar in $(find "$REPO" -name 'egp-utils-*.jar' ! -name '*-sources.jar' ! -name '*-javadoc.jar'); do \
      pom="${jar%.jar}.pom"; \
      if [ ! -f "$pom" ]; then \
        tmp=$(mktemp); \
        if unzip -p "$jar" 'META-INF/maven/com.pccth/egp-utils/pom.xml' > "$tmp" 2>/dev/null && [ -s "$tmp" ]; then cp "$tmp" "$pom"; fi; \
        rm -f "$tmp"; \
      fi; \
    done; \
    echo "=== egp-utils versions baked into image ==="; ls -1 "$REPO"

# ===== app =====
WORKDIR /app
COPY target/*.jar app.jar

EXPOSE 8080
ENTRYPOINT ["java","-jar","app.jar"]
