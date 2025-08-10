plugins {
	java
	id("org.springframework.boot") version "3.5.4"
	id("io.spring.dependency-management") version "1.1.7"

	id("org.flywaydb.flyway") version "11.10.5"
	id("org.jooq.jooq-codegen-gradle") version "3.20.5"
}

buildscript {
	repositories {
		mavenCentral()
	}
	dependencies {
		classpath("org.flywaydb:flyway-database-postgresql:10.15.2")
		classpath("org.postgresql:postgresql:42.7.3")
	}
}

sourceSets {
	main {
		java {
			srcDir("build/generated-sources/jooq")
		}
	}
}

group = "codem.ipsum"
version = "0.0.1-SNAPSHOT"

java {
	toolchain {
		languageVersion = JavaLanguageVersion.of(21)
	}
}

repositories {
	mavenCentral()
}

val postgresContainerName = "postgres-flyway-jooq"
var postgresUser = "build"
var postgresPassword = "build"
var postgresJdbcUrl = "jdbc:postgresql://localhost:5432/build"

flyway {
	url = postgresJdbcUrl
	user = postgresUser
	password = postgresPassword
	locations = arrayOf("filesystem:src/main/resources/db/migration")
}

jooq {
	configurations {
		create("main") {
			configuration {
				logging = org.jooq.meta.jaxb.Logging.DEBUG
				jdbc {
					driver = "org.postgresql.Driver"
					url = postgresJdbcUrl
					user = postgresUser
					password = postgresPassword
				}
				generator {
					name = "org.jooq.codegen.JavaGenerator"
					database {
						name = "org.jooq.meta.postgres.PostgresDatabase"
						includes = ".*"
						excludes = "flyway_schema_history"
						inputSchema = "public"
					}
					target {
						packageName = "codem.ipsum.jooq.generated"
						directory = "build/generated-sources/jooq"
					}
					generate {
						isRecords = true
					}
					strategy {
						name = "org.jooq.codegen.DefaultGeneratorStrategy"
					}
				}
			}
		}
	}
}

tasks.named("compileJava") {
	dependsOn(tasks.named("jooqCodegen"))
}

tasks.named("jooqCodegen") {
	dependsOn(tasks.named("flywayMigrate"))
}

dependencies {
	// REST API
	implementation("org.springframework.boot:spring-boot-starter-web")

	// DB Access
	jooqCodegen("org.postgresql:postgresql:42.7.3")
	implementation("org.springframework.boot:spring-boot-starter-jooq")
	implementation("org.jooq:jooq:3.20.5")
	runtimeOnly("org.postgresql:postgresql")

	// Schema management
	implementation("org.flywaydb:flyway-core")
	implementation("org.flywaydb:flyway-database-postgresql")

	// Tests
	testImplementation("org.springframework.boot:spring-boot-starter-test")
	testImplementation("org.springframework.boot:spring-boot-testcontainers")
	testImplementation("org.testcontainers:junit-jupiter")
	testImplementation("org.testcontainers:postgresql")
	testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

tasks.withType<Test> {
	useJUnitPlatform()
}

// TODO - wait for postgres to start listening on port
val startPostgresTask = tasks.register<Exec>("startPostgres") {
	commandLine("podman",
		"run",
		"--name", postgresContainerName,
		"-d",
		"-p", "5432:5432",
		"-e", "POSTGRES_USER=build",
		"-e", "POSTGRES_PASSWORD=build",
		"-e", "POSTGRES_DB=build",
		"postgres:17.0"
	)
}

// TODO - check whether container exists in the first place
val stopPostgresTask = tasks.register<Exec>("stopPostgres") {
	commandLine("podman",
		"rm",
		"-f",
		postgresContainerName
	)
}
