#!/bin/bash
cd "$(dirname "$0")"

BASE="https://repo1.maven.org/maven2"

# Spring MVC и core
wget -nc "$BASE/org/springframework/spring-webmvc/5.3.39/spring-webmvc-5.3.39.jar"
wget -nc "$BASE/org/springframework/spring-web/5.3.39/spring-web-5.3.39.jar"
wget -nc "$BASE/org/springframework/spring-context/5.3.39/spring-context-5.3.39.jar"
wget -nc "$BASE/org/springframework/spring-core/5.3.39/spring-core-5.3.39.jar"
wget -nc "$BASE/org/springframework/spring-beans/5.3.39/spring-beans-5.3.39.jar"
wget -nc "$BASE/org/springframework/spring-aop/5.3.39/spring-aop-5.3.39.jar"
wget -nc "$BASE/org/springframework/spring-expression/5.3.39/spring-expression-5.3.39.jar"
wget -nc "$BASE/org/springframework/spring-tx/5.3.39/spring-tx-5.3.39.jar"
wget -nc "$BASE/org/springframework/spring-orm/5.3.39/spring-orm-5.3.39.jar"

# Servlet API
wget -nc "$BASE/javax/servlet/javax.servlet-api/4.0.1/javax.servlet-api-4.0.1.jar"

# JSTL
wget -nc "$BASE/javax/servlet/jstl/1.2/jstl-1.2.jar"

# Spring ORM нужен commons-logging
wget -nc "$BASE/commons-logging/commons-logging/1.2/commons-logging-1.2.jar"

# Selenium для системных тестов
wget -nc "$BASE/org/seleniumhq/selenium/selenium-java/4.18.1/selenium-java-4.18.1.jar"
wget -nc "$BASE/org/seleniumhq/selenium/selenium-chrome-driver/4.18.1/selenium-chrome-driver-4.18.1.jar"
wget -nc "$BASE/org/seleniumhq/selenium/selenium-support/4.18.1/selenium-support-4.18.1.jar"
wget -nc "$BASE/org/seleniumhq/selenium/selenium-api/4.18.1/selenium-api-4.18.1.jar"
wget -nc "$BASE/org/seleniumhq/selenium/selenium-remote-driver/4.18.1/selenium-remote-driver-4.18.1.jar"
wget -nc "$BASE/org/seleniumhq/selenium/selenium-http/4.18.1/selenium-http-4.18.1.jar"
wget -nc "$BASE/org/seleniumhq/selenium/selenium-manager/4.18.1/selenium-manager-4.18.1.jar"

# Зависимости Selenium
wget -nc "$BASE/com/google/guava/guava/32.1.3-jre/guava-32.1.3-jre.jar"
wget -nc "$BASE/dev/failsafe/failsafe/3.3.2/failsafe-3.3.2.jar"
wget -nc "$BASE/com/google/code/gson/gson/2.10.1/gson-2.10.1.jar"
wget -nc "$BASE/org/apache/commons/commons-exec/1.3/commons-exec-1.3.jar"
wget -nc "$BASE/io/opentelemetry/opentelemetry-api/1.31.0/opentelemetry-api-1.31.0.jar"
wget -nc "$BASE/io/opentelemetry/opentelemetry-context/1.31.0/opentelemetry-context-1.31.0.jar"
wget -nc "$BASE/io/opentelemetry/opentelemetry-semconv/1.21.0-alpha/opentelemetry-semconv-1.21.0-alpha.jar"

echo "Готово!"
