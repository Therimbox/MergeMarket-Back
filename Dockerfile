# Dockerfile para el backend Spring Boot con Selenium y ChromeDriver
FROM openjdk:17-jdk-slim as builder

# Instala Maven y dependencias necesarias
RUN apt-get update && \
    apt-get install -y maven wget unzip gnupg2 && \
    rm -rf /var/lib/apt/lists/*

WORKDIR /app

# Copia el código fuente y construye el JAR
COPY . .
RUN mvn clean package -DskipTests

# Imagen final
FROM openjdk:17-jdk-slim

# Instala Google Chrome
RUN apt-get update && \
    apt-get install -y wget gnupg2 && \
    wget -q -O - https://dl-ssl.google.com/linux/linux_signing_key.pub | apt-key add - && \
    echo "deb [arch=amd64] http://dl.google.com/linux/chrome/deb/ stable main" > /etc/apt/sources.list.d/google-chrome.list && \
    apt-get update && \
    apt-get install -y google-chrome-stable && \
    rm -rf /var/lib/apt/lists/*

# Instala ChromeDriver (versión compatible con Chrome)
RUN CHROME_VERSION=$(google-chrome --version | awk '{print $3}' | cut -d'.' -f1) && \
    CHROMEDRIVER_VERSION=$(wget -qO- https://chromedriver.storage.googleapis.com/LATEST_RELEASE_$CHROME_VERSION) && \
    wget -O /tmp/chromedriver.zip https://chromedriver.storage.googleapis.com/${CHROMEDRIVER_VERSION}/chromedriver_linux64.zip && \
    unzip /tmp/chromedriver.zip -d /usr/local/bin/ && \
    rm /tmp/chromedriver.zip && \
    chmod +x /usr/local/bin/chromedriver

WORKDIR /app
COPY --from=builder /app/target/MergeMarket-0.0.1-SNAPSHOT.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
