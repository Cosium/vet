import org.gradle.kotlin.dsl.dependencies
import org.gradle.api.tasks.testing.Test
import org.gradle.api.tasks.testing.logging.TestExceptionFormat
import org.gradle.internal.impldep.com.google.common.collect.Lists

plugins {
    java

    application
}

val mainClass = "com.cosium.vet.App"

application {
    mainClassName = mainClass
}

java {
    sourceCompatibility = JavaVersion.VERSION_1_9
    targetCompatibility = JavaVersion.VERSION_1_9
}

dependencies {
    testCompile("junit:junit:4.12")
    testCompile("org.assertj:assertj-core:3.9.0")
    testCompile("org.mockito:mockito-core:2.15.0")
    testCompile("org.apache.httpcomponents:httpclient:4.5.5")
}

repositories {
    mavenLocal()
    jcenter()
}

tasks {
    "test"(Test::class) {
        testLogging {
            exceptionFormat = TestExceptionFormat.FULL
            showStandardStreams = true
        }
    }

    "jar"(Jar::class) {
        manifest {
            attributes["Main-Class"] = mainClass
        }
    }

//------------------------------- jigsaw#start -----------------------------------------------------
    val moduleName = "com.cosium.vet"

    "compileJava"(JavaCompile::class) {
        inputs.property("moduleName", moduleName)
        doFirst {
            options.compilerArgs = listOf(
                    "--module-path", classpath.asPath
            )
            classpath = files()
        }
    }

    "compileTestJava"(JavaCompile::class) {
        inputs.property("moduleName", moduleName)


        doFirst {
            options.compilerArgs = listOf(
                    "--module-path", classpath.asPath,
                    "--add-modules", "junit",
                    "--add-reads", "$moduleName=junit",
                    "--add-reads", "$moduleName=org.mockito",
                    "--add-reads", "$moduleName=assertj.core",
                    "--add-reads", "$moduleName=httpclient",
                    "--add-reads", "$moduleName=httpcore",
                    "--patch-module", "$moduleName=" + files(java.sourceSets["test"].java.srcDirs).asPath
            )
            classpath = files()
        }
    }

    "test"(Test::class) {
        inputs.property("moduleName", moduleName)
        doFirst {
            jvmArgs = listOf(
                    "--module-path", classpath.asPath,
                    "--add-modules", "ALL-MODULE-PATH",
                    "--add-reads", "$moduleName=junit",
                    "--add-reads", "$moduleName=org.mockito",
                    "--add-reads", "$moduleName=assertj.core",
                    "--add-reads", "$moduleName=httpclient",
                    "--add-reads", "$moduleName=httpcore",
                    "--patch-module", "$moduleName=" + files(java.sourceSets["test"].java.outputDir).asPath
            )
            classpath = files()
        }
    }

    "jlink"(Exec::class) {
        dependsOn("build")

        delete("$buildDir/dist")

        workingDir("$buildDir")
        val javaHome = System.getProperty("java.home")!!
        commandLine("$javaHome/bin/jlink", "--module-path", "libs${File.pathSeparatorChar}$javaHome/jmods",
                "--add-modules", moduleName, "--launcher", "vet=$moduleName/$mainClass", "--output", "dist", "--strip-debug",
                "--compress", "2", "--no-header-files", "--no-man-pages")

    }

//------------------------------- jigsaw#end -----------------------------------------------------
}

