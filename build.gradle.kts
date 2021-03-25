import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
	id("org.springframework.boot") version "2.4.3"
	id("io.spring.dependency-management") version "1.0.11.RELEASE"
	war
	kotlin("jvm") version "1.4.30"
	kotlin("plugin.spring") version "1.4.30"
}

group = "jp.annnnnnna"
version = "0.0.1-SNAPSHOT"
java.sourceCompatibility = JavaVersion.VERSION_1_8

repositories {
	mavenCentral()
}

dependencies {
	implementation("org.springframework.boot:spring-boot-starter-thymeleaf")
	implementation("org.springframework.boot:spring-boot-starter-web")
	implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
	implementation("org.jetbrains.kotlin:kotlin-reflect")
	implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
	implementation("org.mariadb.jdbc:mariadb-java-client:2.4.4")
	implementation("org.mybatis:mybatis:3.5.6")
	implementation("org.mybatis:mybatis-spring:2.0.6")
	implementation("org.mybatis:mybatis-typehandlers-jsr310:1.0.2")
	implementation("org.mybatis.spring.boot:mybatis-spring-boot-starter:2.1.2")
	implementation("org.jsoup:jsoup:1.13.1")
	implementation("com.fasterxml.jackson.core:jackson-databind:2.11.4")
	implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.4.3")

	developmentOnly("org.springframework.boot:spring-boot-devtools")
	providedRuntime("org.springframework.boot:spring-boot-starter-tomcat")
	testImplementation("org.springframework.boot:spring-boot-starter-test")
}

tasks.withType<KotlinCompile> {
	kotlinOptions {
		freeCompilerArgs = listOf("-Xjsr305=strict")
		jvmTarget = "1.8"
	}
}

tasks.withType<Test> {
	useJUnitPlatform()
}
