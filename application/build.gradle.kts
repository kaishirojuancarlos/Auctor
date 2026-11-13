import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.compose.desktop.application.dsl.TargetFormat
import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.micronaut.application)
    alias(libs.plugins.micronaut.aot)
    alias(libs.plugins.gradleup.shadow)
    alias(libs.plugins.devtools.ksp)

}

group = "org.kaishiro"
version = "1.0.0"

kotlin {
    jvmToolchain {
        jvmToolchain(25)
    }
}

java {
    sourceCompatibility = JavaVersion.toVersion("25")
    targetCompatibility = JavaVersion.toVersion("25")
}

application {
    mainClass = "org.kaishiro.auctor.MainKt"
    applicationName = "auctor"
}

tasks.shadowJar {
  archiveFileName.set("auctor.jar")
  mergeServiceFiles()
}

/*
distributions {
    main {
        contents {
            from("completions") {
                into("share/zsh/site-functions")
            }
            from("shell") {
                into("share/auctor/shell")
            }
            from("config") {
                into("share/doc/auctor/examples")
            }
        }
    }
}
*/
dependencies {
    implementation(project(":library"))
    testImplementation(project(":library"))
    implementation("io.micronaut:micronaut-context")
    
    testImplementation("org.junit.jupiter:junit-jupiter-api")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine")
}

graalvmNative.toolchainDetection = true // false
graalvmNative {
    binaries {
        create("auctor")
        {
        }
        all {
            buildArgs.add("-H:+SharedArenaSupport")
            buildArgs.add("--enable-native-access=ALL-UNNAMED")
        }
    }
}

micronaut {
    runtime("netty")
    testRuntime("junit5")
    processing {
        incremental(true)
        annotations("com.example.*")
        annotations("org.kaishiro.*")
    }
    aot {
        // Please review carefully the optimizations enabled below
        // Check https://micronaut-projects.github.io/micronaut-aot/latest/guide/ for more details
        convertYamlToJava = false
        precomputeOperations = false
        cacheEnvironment = false
        optimizeClassLoading = true
        deduceEnvironment = false
        optimizeNetty = true
        replaceLogbackXml = true
    }

}

tasks.named<io.micronaut.gradle.docker.MicronautDockerfile>("dockerfile") {
    // baseImage = "eclipse-temurin:25-jre"
    baseImage = "container-registry.oracle.com/graalvm/jdk-community:25"
}

// https://docs.gradle.org/current/userguide/upgrading_major_version_9.html#test_task_fails_when_no_tests_are_discovered
tasks.withType<AbstractTestTask>().configureEach {
    failOnNoDiscoveredTests = false
}

// ###
tasks.test {
    useJUnitPlatform()
}
