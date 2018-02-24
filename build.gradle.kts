import org.gradle.kotlin.dsl.dependencies
import org.gradle.api.tasks.testing.Test
import org.gradle.api.tasks.testing.logging.TestExceptionFormat

plugins {
    // Apply the java plugin to add support for Java
    java

    // Apply the application plugin to add support for building an application
    application
}

application {

    // Define the main class for the application
    mainClassName = "App"
}

java {
    sourceCompatibility = JavaVersion.VERSION_1_9
    targetCompatibility = JavaVersion.VERSION_1_9
}

dependencies {
    // This dependency is found on compile classpath of this component and consumers.
    compile("com.google.guava:guava:23.0")
    compile("commons-cli:commons-cli:1.4")
    compile("org.slf4j:slf4j-api:1.7.25")
    compile("org.apache.commons:commons-lang3:3.7")
    compile("commons-io:commons-io:2.6")
    compile("commons-codec:commons-codec:20041127.091804")

    runtime("org.slf4j:slf4j-simple:1.7.25")

    // Use JUnit test framework
    testCompile("junit:junit:4.12")
    testCompile("org.eclipse.jgit:org.eclipse.jgit.junit:4.10.0.201712302008-r")
    testCompile("org.assertj:assertj-core:3.9.0")
    testCompile("org.mockito:mockito-core:2.15.0")
    testCompile("org.testcontainers:testcontainers:1.6.0")
}

// In this section you declare where to find the dependencies of your project
repositories {
    // Use jcenter for resolving your dependencies.
    // You can declare any Maven/Ivy/file repository here.
    jcenter()
}

tasks.withType<Test> {
    testLogging {
        exceptionFormat = TestExceptionFormat.FULL
        showStandardStreams = true
    }
}