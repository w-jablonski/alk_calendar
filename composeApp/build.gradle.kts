// import java.util.Properties
import org.jetbrains.compose.desktop.application.dsl.TargetFormat
import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi
import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidMultiplatformLibrary)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
    alias(libs.plugins.sqldelight)
}

kotlin {
    @OptIn(ExperimentalWasmDsl::class)
    wasmJs {
        // outputModuleName = "alk"
        browser {
            commonWebpackConfig {
                outputFileName = "alkWasm.js"
            }
        }
        binaries.executable()
    }

    js(IR) {
        // outputModuleName = "alk"
        browser {
            commonWebpackConfig {
                outputFileName = "alkJs.js"
            }
        }
        binaries.executable()
    }

    android {
        namespace = "app.wojablo.alk.lib"
        compileSdk = libs.versions.compileSdk.get().toInt()
        minSdk = libs.versions.minSdk.get().toInt()
        androidResources { enable = true }
        compilerOptions {
            jvmTarget.set(JvmTarget.fromTarget(libs.versions.javaLanguage.get()))
            freeCompilerArgs.add("-Xexpect-actual-classes")
            optIn.add("kotlin.time.ExperimentalTime")
        }
    }

    jvm("desktop")
    applyDefaultHierarchyTemplate()

    sourceSets {
        all {
            kotlin.exclude("**/.*", "**/.*/*")
        }
        /*
        val jvmShared by creating {
            dependsOn(commonMain.get())
        } */
        val androidMain by getting {
            // dependsOn(jvmShared)
        }
        val desktopMain by getting {
            // dependsOn(jvmShared)
        }
        commonMain.dependencies {
            implementation(libs.compose.foundation)
            implementation(compose.material3)
            // implementation(libs.compose.material3)
            implementation(libs.compose.runtime)
            implementation(libs.compose.ui)
            implementation(libs.compose.components.resources)
            implementation(libs.compose.components.ui.tooling.preview)
            implementation(libs.calendar.multiplatform)
            implementation(libs.compose.navigation)
            implementation(libs.material.icons)
            // implementation(libs.sqldelight.coroutines.extensions)
            implementation(libs.sqldelight.runtime)
        }
        desktopMain.dependencies {
            implementation(compose.desktop.currentOs)
            implementation(libs.sqldelight.sqlite.driver)
        }
        androidMain.dependencies {
            implementation(compose.preview)
            implementation(libs.sqldelight.android.driver)
        }
        wasmJsMain.dependencies {
            // implementation(libs.sqldelight.web.worker.driver)
            implementation(npm("@cashapp/sqldelight-sqljs-worker", "2.1.0"))
            implementation(npm("sql.js", "1.8.0"))
        }
        jsMain.dependencies {
            implementation(npm("@cashapp/sqldelight-sqljs-worker", "2.1.0"))
            implementation(npm("sql.js", "1.8.0"))
        }
    }

    /*
    @OptIn(ExperimentalKotlinGradlePluginApi::class)
    compilerOptions {
        freeCompilerArgs.add("-Xexpect-actual-classes")
        optIn.add("kotlin.time.ExperimentalTime")
    } */
}



compose.desktop {
    application {
        mainClass = "MainKt"
        nativeDistributions {
            packageName = "app.wojablo.alk"
            packageVersion = libs.versions.versionName.get()
            targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)
            linux {
                iconFile.set(project.file("src/desktopMain/resources/icon.png"))
            }
        }
    }
}

tasks.register<Copy>("publishWasm") {
    dependsOn("wasmJsBrowserDistribution")
    from("build/dist/wasmJs/productionExecutable")
    into("${rootProject.projectDir}/docs")
}

// less reliable
/*
tasks.register<Copy>("publishJs") {
    dependsOn("jsBrowserDistribution")
    from("build/dist/js/productionExecutable")
    into("${rootProject.projectDir}/docs")
} */

tasks.register<Copy>("publishApk") {
    //dependsOn("assembleRelease")
    dependsOn(":androidApp:assembleRelease")
    //from("build/outputs/apk/release")
    from("${rootProject.projectDir}/androidApp/build/outputs/apk/release")
    into("${rootProject.projectDir}/release")
    include("*.apk")
    rename { fileName ->
        // "alk-${libs.versions.versionName.get()}.apk"
        "alk.apk"
    }
}

sqldelight {
    databases {
        create("CalendarDatabase") {
            packageName.set("app.wojablo.alk")
            generateAsync.set(true)

        }
    }
}
