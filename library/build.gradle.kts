import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.compose.desktop.application.dsl.TargetFormat

plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.kotlin.allopen)
    alias(libs.plugins.devtools.ksp)
    alias(libs.plugins.micronaut.library)
    alias(libs.plugins.micronaut.aot)

    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.compose.multiplatform)
    alias(libs.plugins.kotlinx.serialisation)
}

group = "org.kaishiro"
version = "1.0.0"

val kotlinVersion= project.properties["kotlinVersion"]

kotlin {
    jvmToolchain {
        jvmToolchain(25)
    }
}

java {
    sourceCompatibility = JavaVersion.toVersion("25")
    targetCompatibility = JavaVersion.toVersion("25")
}
/*
application {
    mainClass = "com.example.ApplicationKt"
    applicationName = "auctor"
}*/
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
}*/

compose.desktop {
    application {
        mainClass = "org.kaishiro.auctor.core.MainKt"
        // applicationName = "auctor"
        
        nativeDistributions {
            targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)
            packageName = "Auctor"
            packageVersion = "1.0.0"

            linux {
                iconFile.set(project.file("appIcons/LinuxIcon.png"))
            }
            windows {
                iconFile.set(project.file("appIcons/WindowsIcon.ico"))
            }
            macOS {
                iconFile.set(project.file("appIcons/MacosIcon.icns"))
                bundleID = "org.kaishiro.auctor"
            }
        }
    }
}

dependencies {
    api(libs.compose.runtime)
    api(libs.compose.ui)
    api(libs.compose.foundation)
    api(libs.compose.resources)
    api(libs.compose.ui.tooling.preview)
    api(libs.compose.material3)
    implementation(libs.kotlinx.coroutines.core)
    implementation(libs.androidx.lifecycle.viewmodel)
    implementation(libs.androidx.lifecycle.runtime)
    implementation(libs.androidx.lifecycle.viewmodel.navigation3)
    implementation(libs.compose.nav3)
    implementation(libs.kotlinx.serialization.json)
    implementation(libs.materialKolor)
    // #
    implementation(compose.desktop.currentOs)
    implementation(libs.kotlinx.coroutines.swing)
    // Test dependencies
    implementation(kotlin("test"))
    implementation(libs.compose.ui.test)
    implementation(libs.kotlinx.coroutines.test)
    // ###
    ksp("io.micronaut:micronaut-http-validation")
    ksp("io.micronaut.serde:micronaut-serde-processor")
    implementation("io.micronaut.kotlin:micronaut-kotlin-runtime")
    implementation("io.micronaut.serde:micronaut-serde-jackson")
    implementation("org.jetbrains.kotlin:kotlin-reflect:${kotlinVersion}")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8:${kotlinVersion}")
    compileOnly("io.micronaut:micronaut-http-client")
    runtimeOnly("ch.qos.logback:logback-classic")
    runtimeOnly("tools.jackson.module:jackson-module-kotlin")
    testImplementation("io.micronaut:micronaut-http-client")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
    // ########
    implementation("io.micronaut:micronaut-context")
    // ###
    testImplementation("org.junit.jupiter:junit-jupiter-api")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine")
    // ##
    testImplementation(platform("org.junit:junit-bom:6.1.3"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    // #########
    implementation("com.github.ajalt.clikt:clikt:5.1.0")
    // ###
    implementation("com.akuleshov7:ktoml-core:0.7.1")
    implementation("com.akuleshov7:ktoml-file:0.7.1")
    // ###
    implementation("org.jetbrains.compose.material:material-icons-extended")
}

graalvmNative.toolchainDetection = true // false
graalvmNative {
    binaries {
        create("auctor-library")
        {
        }
        all {
            buildArgs.add("-H:+SharedArenaSupport")
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

// https://docs.gradle.org/current/userguide/upgrading_major_version_9.html#test_task_fails_when_no_tests_are_discovered
tasks.withType<AbstractTestTask>().configureEach {
    failOnNoDiscoveredTests = false
}

// ###
tasks.test {
    useJUnitPlatform()
}
