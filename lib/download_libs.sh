#!/bin/bash

# Скрипт для скачивания необходимых JAR-библиотек

# Переход в папку lib
cd lib

# Maven Central базовый URL
MAVEN_URL="https://repo1.maven.org/maven2"

# Функция скачивания
download_jar() {
    local group=$1
    local artifact=$2
    local version=$3
    local filename="${artifact}-${version}.jar"
    
    # Преобразование group в путь (com.example -> com/example)
    local group_path=$(echo $group | tr '.' '/')
    
    local url="${MAVEN_URL}/${group_path}/${artifact}/${version}/${filename}"
    
    echo "Скачивание ${filename}..."
    wget -q --show-progress "${url}" -O "${filename}"
    
    if [ $? -eq 0 ]; then
        echo "✓ ${filename} скачан"
    else
        echo "✗ Ошибка скачивания ${filename}"
    fi
}

echo "===== Начало скачивания библиотек ====="

# Hibernate Core
download_jar "org/hibernate" "hibernate-core" "5.6.15.Final"

# Hibernate Commons Annotations
download_jar "org/hibernate/common" "hibernate-commons-annotations" "5.1.2.Final"

# JBoss Logging
download_jar "org/jboss/logging" "jboss-logging" "3.4.3.Final"

# JPA API
download_jar "javax/persistence" "javax.persistence-api" "2.2"

# JTA
download_jar "javax/transaction" "jta" "1.1"

# ANTLR
download_jar "antlr" "antlr" "2.7.7"

# Byte Buddy
download_jar "net/bytebuddy" "byte-buddy" "1.12.18"

# ClassMate
download_jar "com/fasterxml" "classmate" "1.5.1"

# Jandex
download_jar "org/jboss" "jandex" "2.4.2.Final"

# Dom4j
download_jar "org/dom4j" "dom4j" "2.1.3"

# TestNG
download_jar "org/testng" "testng" "7.7.1"

# JCommander (зависимость TestNG)
download_jar "com/beust" "jcommander" "1.82"

# SLF4J API
download_jar "org/slf4j" "slf4j-api" "1.7.36"

# SLF4J Simple (реализация для логирования)
download_jar "org/slf4j" "slf4j-simple" "1.7.36"

echo "===== Скачивание завершено ====="
echo ""
echo "Скачанные файлы:"
ls -lh *.jar
