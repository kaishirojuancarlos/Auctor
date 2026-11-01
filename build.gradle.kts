plugins {
  alias(libs.plugins.kotlin.jvm) apply false
  alias(libs.plugins.kotlin.allopen) apply false
  alias(libs.plugins.devtools.ksp) apply false
  alias(libs.plugins.micronaut.library) apply false
  alias(libs.plugins.micronaut.application) apply false
  alias(libs.plugins.gradleup.shadow) apply false
  alias(libs.plugins.micronaut.aot) apply false

    // alias(libs.plugins.kotlin.multiplatform).apply(false)
    alias(libs.plugins.compose.compiler).apply(false)
    alias(libs.plugins.compose.multiplatform).apply(false)
    // alias(libs.plugins.kotlin.jvm).apply(false)
    alias(libs.plugins.kotlinx.serialisation).apply(false)
}
