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

# Instala Google Chrome y unzip
RUN apt-get update && \
    apt-get install -y wget gnupg2 unzip && \
    wget -q -O - https://dl-ssl.google.com/linux/linux_signing_key.pub | apt-key add - && \
    echo "deb [arch=amd64] http://dl.google.com/linux/chrome/deb/ stable main" > /etc/apt/sources.list.d/google-chrome.list && \
    apt-get update && \
    apt-get install -y google-chrome-stable && \
    rm -rf /var/lib/apt/lists/*

RUN echo "=== CHROME VERSION ===" && google-chrome --version && echo "====================="

# Instala ChromeDriver versión 142 (compatible con Chrome 142)
RUN wget -O /tmp/chromedriver.zip https://edgedl.me.gvt1.com/edgedl/chrome/chrome-for-testing/142.0.7444.59/linux64/chromedriver-linux64.zip && \
    unzip /tmp/chromedriver.zip -d /usr/local/bin/ && \
    mv /usr/local/bin/chromedriver-linux64/chromedriver /usr/local/bin/chromedriver && \
    rm -rf /usr/local/bin/chromedriver-linux64 /tmp/chromedriver.zip && \
    chmod +x /usr/local/bin/chromedriver

# Add missing dependencies for Google Chrome
RUN apt-get update && apt-get install -y \
    fonts-liberation \
    libappindicator3-1 \
    libasound2 \
    libatk-bridge2.0-0 \
    libatk1.0-0 \
    libcups2 \
    libdbus-1-3 \
    libgdk-pixbuf2.0-0 \
    libnspr4 \
    libnss3 \
    libx11-xcb1 \
    libxcomposite1 \
    libxdamage1 \
    libxrandr2 \
    xdg-utils \
    --no-install-recommends && \
    rm -rf /var/lib/apt/lists/*

# Copy chromedriver to the expected path
RUN mkdir -p /app/drivers && cp /usr/local/bin/chromedriver /app/drivers/chromedriver

WORKDIR /app
COPY --from=builder /app/target/MergeMarket-0.0.1-SNAPSHOT.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
