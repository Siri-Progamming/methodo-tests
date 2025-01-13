plugins {
	kotlin("jvm") version "1.9.25"
	kotlin("plugin.spring") version "1.9.25"
	id("org.springframework.boot") version "3.4.0"
	id("io.spring.dependency-management") version "1.1.6"
	id("jacoco")
	id("info.solidsoft.pitest") version "1.15.0"
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
	implementation("org.springframework.boot:spring-boot-starter")
	implementation("org.jetbrains.kotlin:kotlin-reflect")
	testImplementation("org.springframework.boot:spring-boot-starter-test")
	testImplementation("org.jetbrains.kotlin:kotlin-test-junit5")
	testRuntimeOnly("org.junit.platform:junit-platform-launcher")
	testImplementation("io.kotest:kotest-assertions-core-jvm:5.9.1")
	testImplementation("io.kotest:kotest-runner-junit5-jvm:5.9.1")
	testImplementation("io.kotest:kotest-property:5.9.1")
	testImplementation("io.mockk:mockk:1.13.8")
	testImplementation("org.pitest:pitest-junit5-plugin:1.1.2")
}

kotlin {
	jvmToolchain(21)
	compilerOptions {
		freeCompilerArgs.addAll("-Xjsr305=strict")
	}
}

pitest {
	junit5PluginVersion.set("1.1.2")
	timestampedReports.set(false)
	threads.set(4)
	outputFormats.set(listOf("HTML", "XML"))

	targetClasses.set(listOf(
		"kat.siri.*"
	))

	mutators.set(listOf(
		"CONDITIONALS_BOUNDARY",
		"INCREMENTS",
		"INVERT_NEGS",
		"MATH",
		"NEGATE_CONDITIONALS",
		"VOID_METHOD_CALLS",
		"EMPTY_RETURNS",
		"FALSE_RETURNS",
		"TRUE_RETURNS",
		"NULL_RETURNS",
		"PRIMITIVE_RETURNS"
	))

	excludedClasses.set(listOf(
		"kat.siri.config.*",
		"kat.siri.Application"
	))

	mutationThreshold.set(70)
	coverageThreshold.set(70)
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
