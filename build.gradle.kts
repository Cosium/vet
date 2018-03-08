import de.undercouch.gradle.tasks.download.Download
import org.gradle.api.tasks.Exec
import org.gradle.kotlin.dsl.dependencies
import org.gradle.api.tasks.testing.Test
import org.gradle.api.tasks.testing.logging.TestExceptionFormat
import org.gradle.internal.impldep.com.esotericsoftware.kryo.util.Util.string
import org.gradle.internal.impldep.com.google.common.collect.Lists
import org.gradle.internal.impldep.com.google.common.io.Files
import org.gradle.internal.impldep.com.jcraft.jsch.MAC
import java.io.File

plugins {
    java

    application

    maven

    signing

    id("de.undercouch.download").version("3.3.0")
}

group = "com.cosium.vet"
version = "1.4"

val mainClass = "com.cosium.vet.App"
val ossrhUsername by project
val ossrhPassword by project

enum class OS(val alias: String, val dirname: String, val jvmArchiveName: String, val jvmArchiveRootDir: String, val jvmUrl: String) {
    LINUX_X64(
            "LinuxX64",
            "linux_x64",
            "linux_x64.tar.gz",
            "zulu9.0.4.1-jdk9.0.4-linux_x64",
            "https://cdn.azul.com/zulu/bin/zulu9.0.4.1-jdk9.0.4-linux_x64.tar.gz"
    ),
    WINDOWS_X64(
            "WindowsX64",
            "windows_x64",
            "win_x64.zip",
            "zulu9.0.4.1-jdk9.0.4-win_x64",
            "https://cdn.azul.com/zulu/bin/zulu9.0.4.1-jdk9.0.4-win_x64.zip"
    ),
    MACOSX_X64(
            "MacX64",
            "macosx_x64",
            "macosx_x64.zip",
            "zulu9.0.4.1-jdk9.0.4-macosx_x64",
            "https://cdn.azul.com/zulu/bin/zulu9.0.4.1-jdk9.0.4-macosx_x64.zip"
    );
}

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

signing {
    gradle.taskGraph.whenReady {
        isRequired = this.hasTask("uploadArchives")
    }
    sign(configurations.archives)
}

tasks {
    "test"(Test::class) {
        testLogging {
            exceptionFormat = TestExceptionFormat.FULL
            showStandardStreams = true
        }
    }

    "javadocJar"(Jar::class) {
        classifier = "javadoc"
        from("javadoc")
    }

    "sourcesJar"(Jar::class) {
        classifier = "sources"
        from(java.sourceSets["main"].allSource)
    }

    artifacts {
        add("archives", tasks.getByName("javadocJar"))
        add("archives", tasks.getByName("sourcesJar"))
    }

    "jar"(Jar::class) {
        manifest {
            attributes["Main-Class"] = mainClass
        }
    }

    "addVersionFile"(Task::class) {
        dependsOn("processResources")
        doLast {
            val versionFile = File("$buildDir/resources/main/com/cosium/vet/version.txt")
            versionFile.parentFile.mkdirs()
            versionFile.createNewFile()
            versionFile.writeText(project.version.toString())
        }
    }

    "classes"(Task::class) {
        dependsOn("addVersionFile")
    }

//------------------------------- jigsaw#start -----------------------------------------------------------------------------
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


    val binariesGroup = "binaries"

    val buildModulePath = "buildModulePath"(Copy::class) {
        this.group = binariesGroup
        from(tasks.getByName("jar"))
        into("$buildDir/module-path")
    }

    val createBinariesTasks = OS.values().map { os ->
        tasks.create("createBinaries${os.alias}") {
            this.group = binariesGroup

            val downloadJvm = "downloadJvm${os.alias}"(Download::class) {
                this.group = binariesGroup
                src(os.jvmUrl)
                dest("$buildDir/jvm/${os.jvmArchiveName}")
            }

            val unzipJvm = "unzipJvm${os.alias}"(Copy::class) {
                this.group = binariesGroup

                dependsOn(downloadJvm)
                if (os.jvmArchiveName.contains(".tar")) {
                    from(tarTree(downloadJvm.dest))
                } else {
                    from(zipTree(downloadJvm.dest))
                }
                into("$buildDir/jvm")
            }

            val binariesOutput = "binaries/${os.dirname}"
            val deletePreviousBinariesOutput = "deletePreviousBinariesOutput${os.alias}"(Delete::class){
                delete("$buildDir/$binariesOutput")
            }

            val jlink = "jlinkJvm${os.alias}"(Exec::class) {
                this.group = binariesGroup

                dependsOn("build")
                dependsOn(buildModulePath)
                dependsOn(unzipJvm)
                dependsOn(deletePreviousBinariesOutput)

                workingDir("$buildDir")

                val javaHome = System.getProperty("java.home")!!

                commandLine("$javaHome/bin/jlink",
                        "--module-path", "module-path${File.pathSeparatorChar}${unzipJvm.destinationDir}/${os.jvmArchiveRootDir}/jmods",
                        "--add-modules", moduleName,
                        "--launcher", "${project.name}=$moduleName/$mainClass",
                        "--output", binariesOutput,
                        "--strip-debug",
                        "--compress", "2",
                        "--no-header-files",
                        "--no-man-pages")
            }

            val overrideLauncher = "overrideLauncher${os.alias}"(Copy::class){
                dependsOn(jlink)
                from("$rootDir/launcher-override.sh"){
                    this.rename("launcher-override\\.sh", "vet")
                }
                into("$buildDir/$binariesOutput/bin")
            }

            val zipBinaries = "zipBinaries${os.alias}"(Zip::class){
                this.group = binariesGroup

                dependsOn(overrideLauncher)

                from("$buildDir/$binariesOutput")
                include("**/*")
                archiveName = "vet-${os.dirname}.zip"
                destinationDir = File("$buildDir/binaries")
            }

            val cleanup = "cleanup${os.alias}"(Delete::class){
                this.group = binariesGroup

                dependsOn(zipBinaries)
                delete("$buildDir/$binariesOutput")
            }

            dependsOn(cleanup)
        }
    }
    "binaries" {
        this.group = binariesGroup
        createBinariesTasks.forEach({ t ->
            dependsOn(t)
        })
    }

//------------------------------- jigsaw#end -----------------------------------------------------------------------------------

//---------------------------- oss-sonatype#start ------------------------------------------------------------------------------
    "uploadArchives"(Upload::class) {
        repositories {

            withConvention(MavenRepositoryHandlerConvention::class) {

                mavenDeployer {
                    beforeDeployment {
                        signing.signPom(this)
                    }

                    withGroovyBuilder {
                        "repository"("url" to uri("https://oss.sonatype.org/service/local/staging/deploy/maven2/")) {
                            "authentication"("userName" to "$ossrhUsername", "password" to "$ossrhPassword")
                        }
                        "snapshotRepository"("url" to uri("https://oss.sonatype.org/content/repositories/snapshots/")) {
                            "authentication"("userName" to "$ossrhUsername", "password" to "$ossrhPassword")
                        }

                        "pom" {
                            "project"{
                                "name"(project.name)
                                "artifactId"(project.name)
                                "packaging"("jar")
                                "description"("Gerrit client using pull request review workflow")
                                "url"("https://github.com/Cosium/${project.name}")
                                "scm"{
                                    "connection"("scm:git:https://github.com/Cosium/${project.name}")
                                    "developerConnection"("scm:git:https://github.com/Cosium/${project.name}")
                                    "url"("https://github.com/Cosium/${project.name}")
                                }
                                "licenses"{
                                    "license" {
                                        "name"("MIT License")
                                        "url"("http://www.opensource.org/licenses/mit-license.php")
                                    }
                                }
                                "developers" {
                                    "developer" {
                                        "id"("reda-alaoui")
                                        "name"("Réda Housni Alaoui")
                                        "email"("reda.housnialaoui@cosium.com")
                                        "url"("https://github.com/reda-alaoui")
                                    }
                                }
                            }
                        }
                    }
                }

            }

        }

    }
//---------------------------- oss-sonatype#end ------------------------------------------------------------------------------


}

