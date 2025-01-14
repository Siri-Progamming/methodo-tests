val testIntegrationImplementation: Configuration by configurations.creating {
	extendsFrom(configurations.implementation.get())
}
val testComponentImplementation: Configuration by configurations.creating {
	extendsFrom(configurations.implementation.get())
}

plugins {
	kotlin("jvm") version "1.9.25"
	kotlin("plugin.spring") version "1.9.25"
	kotlin("plugin.jpa") version "1.9.10"
	id("org.springframework.boot") version "3.4.0"
	id("io.spring.dependency-management") version "1.1.6"
	id("jacoco")
//	id("info.solidsoft.pitest") version "1.15.0"
}

group = "kat.siri"
version = "0.0.1-SNAPSHOT"

java {
	toolchain {
		languageVersion = JavaLanguageVersion.of(21)
	}
}

buildscript {
	repositories {
		mavenCentral {
			isAllowInsecureProtocol = true
		}
	}
}

repositories {
	mavenCentral {
		isAllowInsecureProtocol = true
	}
}

dependencies {
	implementation("org.springframework.boot:spring-boot-starter") // Spring Boot Starter (base)
	implementation("org.springframework.boot:spring-boot-starter-web") // Spring Boot Web
	implementation("org.springframework.boot:spring-boot-starter-validation") // Annotations de validation des données

	implementation("org.springframework.boot:spring-boot-starter-data-jpa") // JPA pour ORM
	implementation("org.jetbrains.kotlin:kotlin-reflect")
	implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")

	implementation("org.liquibase:liquibase-core")
	implementation("org.postgresql:postgresql") // Driver BDD

	// T E S T S
	testRuntimeOnly("org.junit.platform:junit-platform-launcher")
	testImplementation("org.springframework.boot:spring-boot-starter-test")
	testImplementation("org.hibernate.validator:hibernate-validator")
	testImplementation("org.jetbrains.kotlin:kotlin-test-junit5")
	testImplementation("io.kotest:kotest-assertions-core-jvm:5.9.1")
	testImplementation("io.kotest:kotest-runner-junit5-jvm:5.9.1")
	testImplementation("io.kotest:kotest-property:5.9.1")
	testImplementation("io.mockk:mockk:1.13.13")
//	testImplementation("org.pitest:pitest-junit5-plugin:1.1.2")

	// T E S T S   I N T E G R A T I O N
	testIntegrationImplementation("org.testcontainers:postgresql:1.19.1")
	testIntegrationImplementation("org.testcontainers:jdbc-test:1.12.0")
	testIntegrationImplementation("org.testcontainers:testcontainers:1.19.1")
	testIntegrationImplementation("io.kotest.extensions:kotest-extensions-testcontainers:2.0.2")
	testIntegrationImplementation("io.mockk:mockk:1.13.8")
	testIntegrationImplementation("io.kotest:kotest-assertions-core:5.9.1")
	testIntegrationImplementation("io.kotest:kotest-runner-junit5:5.9.1")
	testIntegrationImplementation("com.ninja-squad:springmockk:4.0.2")
	testIntegrationImplementation("org.springframework.boot:spring-boot-starter-test") {
		exclude(module = "mockito-core")
	}

	// T E S T S   C O M P O N E N T S
	testComponentImplementation("io.cucumber:cucumber-java:7.14.0")
	testComponentImplementation("io.cucumber:cucumber-spring:7.14.0")
	testComponentImplementation("io.cucumber:cucumber-junit:7.14.0")
	testComponentImplementation("io.cucumber:cucumber-junit-platform-engine:7.14.0")
	testComponentImplementation("io.rest-assured:rest-assured:5.3.2")
	testComponentImplementation("org.junit.platform:junit-platform-suite:1.10.0")
	testComponentImplementation("org.testcontainers:postgresql:1.19.1")
	testComponentImplementation("io.kotest:kotest-assertions-core:5.9.1")
	testComponentImplementation("org.springframework.boot:spring-boot-starter-test")
	testComponentImplementation("org.springframework.boot:spring-boot-starter-web")
}

kotlin {
	compilerOptions {
		freeCompilerArgs.addAll("-Xjsr305=strict")
	}
}

//Commenté car proxy ordinateur entreprise
//pitest {
//	junit5PluginVersion.set("1.1.2")
//	timestampedReports.set(false)
//	threads.set(4)
//	outputFormats.set(listOf("HTML", "XML"))
//
//	targetClasses.set(listOf(
//		"kat.siri.*"
//	))
//
//	mutators.set(listOf(
//		"CONDITIONALS_BOUNDARY",
//		"INCREMENTS",
//		"INVERT_NEGS",
//		"MATH",
//		"NEGATE_CONDITIONALS",
//		"VOID_METHOD_CALLS",
//		"EMPTY_RETURNS",
//		"FALSE_RETURNS",
//		"TRUE_RETURNS",
//		"NULL_RETURNS",
//		"PRIMITIVE_RETURNS"
//	))
//
//	excludedClasses.set(listOf(
//		"kat.siri.config.*",
//		"kat.siri.Application"
//	))
//
//	mutationThreshold.set(70)
//	coverageThreshold.set(70)
//}

//Pour ajouter les commandes gradle testIntegration & testComponent
testing {
	suites {
		val testIntegration by registering(JvmTestSuite::class) {
			sources {
				kotlin {
					setSrcDirs(listOf("src/testIntegration/kotlin"))
				}
				compileClasspath += sourceSets.main.get().output
				runtimeClasspath += sourceSets.main.get().output
			}
		}
	}
	suites {
		val testComponent by registering(JvmTestSuite::class) {
			sources {
				kotlin {
					setSrcDirs(listOf("src/testComponent/kotlin"))
				}
				compileClasspath += sourceSets.main.get().output
				runtimeClasspath += sourceSets.main.get().output
			}
		}
	}
}

tasks.withType<Test> {
	useJUnitPlatform()
}
tasks.jacocoTestReport {
	reports {
		xml.required.set(true)
		html.required.set(true)
	}
}
tasks.test {
	finalizedBy(tasks.jacocoTestReport)
}
