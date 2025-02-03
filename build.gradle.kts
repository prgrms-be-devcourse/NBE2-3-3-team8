plugins {
    id("org.springframework.boot") version "3.4.0"
    id("io.spring.dependency-management") version "1.1.6"

    kotlin("jvm") version "1.9.25"
    kotlin("plugin.spring") version "1.9.25"
    kotlin("plugin.jpa") version "1.9.25"
    kotlin("kapt") version "1.9.25"
    kotlin("plugin.allopen") version "1.9.25"
    kotlin("plugin.noarg") version "1.9.25"
}

allOpen {
    annotation("javax.persistence.Entity")
    annotation("jakarta.persistence.Embeddable")
    annotation("jakarta.persistence.MappedSuperclass")
}

noArg {
    annotation("jakarta.persistence.Entity")
    annotation("jakarta.persistence.MappedSuperclass")
    annotation("jakarta.persistence.Embeddable")
}

group = "org.programmers"
version = "0.0.1-SNAPSHOT"

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(17)
    }
}

configurations {
    compileOnly {
        extendsFrom(configurations.annotationProcessor.get())
    }
}

repositories {
    mavenCentral()
    maven("https://jitpack.io")
}

dependencies {
    // Web
    implementation ("org.springframework.boot:spring-boot-starter-web")
    implementation ("org.springframework.boot:spring-boot-starter-validation")
    implementation ("org.springframework.boot:spring-boot-starter-webflux")
    implementation ("org.springframework.boot:spring-boot-starter-websocket")

    // Lombok
    annotationProcessor ("org.projectlombok:lombok")
    compileOnly ("org.projectlombok:lombok")

    // DB
    implementation ("org.springframework.boot:spring-boot-starter-data-redis")
    runtimeOnly ("org.mariadb.jdbc:mariadb-java-client")
    implementation ("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation ("org.springframework.boot:spring-boot-starter-data-jdbc")

    // Scheduling Lock
    implementation ("net.javacrumbs.shedlock:shedlock-spring:6.2.0")
    implementation ("net.javacrumbs.shedlock:shedlock-provider-redis-spring:6.2.0")

    // Test
    testImplementation ("org.springframework.boot:spring-boot-starter-test")
    testRuntimeOnly ("org.junit.platform:junit-platform-launcher")
    testImplementation ("org.springframework.boot:spring-boot-testcontainers")
    testImplementation ("org.testcontainers:testcontainers")
    testImplementation ("org.testcontainers:junit-jupiter")
    testImplementation ("org.testcontainers:mariadb:1.19.7")
    testImplementation ("org.springframework.security:spring-security-test")

    // Swagger
    implementation ("org.springdoc:springdoc-openapi-starter-webmvc-ui:2.7.0")

    // Hibernate-spatial
    implementation ("org.hibernate:hibernate-spatial:6.6.3.Final")

    // Mapstuct
    implementation ("org.mapstruct:mapstruct:1.6.3")
    kapt ("org.mapstruct:mapstruct-processor:1.6.3")
    kaptTest ("org.mapstruct:mapstruct-processor:1.6.3")

    // QueryDSL
    implementation("com.querydsl:querydsl-jpa:5.1.0:jakarta")
    kapt("com.querydsl:querydsl-apt:5.1.0:jakarta")
    kapt("jakarta.annotation:jakarta.annotation-api")
    kapt("jakarta.persistence:jakarta.persistence-api")

    // Thymeleaf
    implementation ("org.springframework.boot:spring-boot-starter-thymeleaf")
    implementation ("nz.net.ultraq.thymeleaf:thymeleaf-layout-dialect")

    // Batch
    implementation ("org.springframework.boot:spring-boot-starter-batch")
    testImplementation ("org.springframework.batch:spring-batch-test")

    // Security
    implementation ("org.springframework.boot:spring-boot-starter-security")
    implementation ("org.thymeleaf.extras:thymeleaf-extras-springsecurity6")

    // oAuth2
    implementation ("org.springframework.boot:spring-boot-starter-oauth2-resource-server")
    implementation ("org.springframework.boot:spring-boot-starter-oauth2-authorization-server")
    implementation ("org.springframework.boot:spring-boot-starter-oauth2-client")

    // Session
    implementation ("org.springframework.session:spring-session-data-redis")

    // Monitoring
    implementation ("org.springframework.boot:spring-boot-starter-actuator")
    runtimeOnly ("io.micrometer:micrometer-registry-prometheus")

    // Amazon S3
    implementation ("software.amazon.awssdk:s3:2.30.3")

    // Monitoring
    implementation ("org.springframework.boot:spring-boot-starter-actuator")

    // Kotlin
    implementation ("org.jetbrains.kotlin:kotlin-stdlib")
    implementation ("org.jetbrains.kotlin:kotlin-reflect:1.9.25")
    implementation ("io.github.oshai:kotlin-logging-jvm:7.0.3")

    // toString(), equals(), hashCode() 구현을 위해 사용할 라이브러리
    implementation("com.github.consoleau:kassava:2.1.0")
}

// 테스트 환경 설정
tasks.named<Test>("test") {
    systemProperty("spring.profiles.active", "test")
    useJUnitPlatform()
}

// YAML 복사 작업 설정
tasks.register<Copy>("copyYaml") {
    from("./submodule/config")
    include("*.yml")
    into("src/main/resources")
}

tasks.named<ProcessResources>("processResources") {
    dependsOn("copyYaml")
}

// Querydsl 설정부 추가
val generated = file("src/main/generated")

// querydsl QClass 파일 생성 위치를 지정
tasks.withType<JavaCompile> {
    options.generatedSourceOutputDirectory.set(generated)
}

// kotlin source set 에 querydsl QClass 위치 추가
sourceSets {
    main {
        kotlin.srcDirs += generated
    }
}

// gradle clean 시에 QClass 디렉토리 삭제
tasks.named("clean") {
    doLast {
        generated.deleteRecursively()
    }
}

kapt {
    generateStubs = true
}