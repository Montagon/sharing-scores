plugins {
    application
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.kotlinx.serialization)
    alias(libs.plugins.spotless)
}

group = "org.jcma"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation(libs.bundles.arrow)
    implementation(libs.bundles.google)
    implementation(libs.bundles.ktor.client)
    implementation(libs.bundles.ktor.server)
    implementation(libs.bundles.tegral)
    implementation(libs.ktor.http)
    implementation(libs.ktor.utils)
    implementation(libs.logback)
    testImplementation("org.jetbrains.kotlin:kotlin-test")
}

spotless {
    kotlin {
        target("**/*.kt")
        ktfmt().googleStyle().configure { it.setRemoveUnusedImport(true) }
    }
}

tasks.test {
    useJUnitPlatform()
}
kotlin {
    jvmToolchain(17)
}

task<JavaExec>("sharing-scores") {
    dependsOn("compileKotlin")
    group = "Execution"
    description = "Runs the Sharing Scores server"
    classpath = sourceSets.main.get().runtimeClasspath
    mainClass.set("org.jcma.sharingscores.SharingScoresServer")
}
