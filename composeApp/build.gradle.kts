import java.util.Properties
import org.jetbrains.compose.desktop.application.dsl.TargetFormat
import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi
import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
    alias(libs.plugins.composeHotReload)
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

    androidTarget {}
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
            implementation(compose.foundation)
            implementation(compose.material3)
            implementation(compose.runtime)
            implementation(compose.ui)
            implementation(compose.components.resources)
            implementation(compose.components.uiToolingPreview)
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
    @OptIn(ExperimentalKotlinGradlePluginApi::class)
    compilerOptions {
        freeCompilerArgs.add("-Xexpect-actual-classes")
        optIn.add("kotlin.time.ExperimentalTime")
    }
}

android {
    namespace = "app.wojablo.alk"
    compileSdk = libs.versions.compileSdk.get().toInt()
    defaultConfig {
        applicationId = "app.wojablo.alk"
        minSdk = libs.versions.minSdk.get().toInt()
        targetSdk = libs.versions.targetSdk.get().toInt()
        versionCode = libs.versions.versionCode.get().toInt()
        versionName = libs.versions.versionName.get()
    }
    signingConfigs {
        create("release") {
            val propsFile = rootProject.file("local.properties")
            if (!propsFile.exists()) error("local.properties not found")

            val props = Properties().apply {
                load(propsFile.inputStream())
            }
            storeFile = file(props.getProperty("RELEASE_STORE_FILE"))
            storePassword = props.getProperty("RELEASE_STORE_PASSWORD")
            keyAlias = props.getProperty("RELEASE_KEY_ALIAS")
            keyPassword = props.getProperty("RELEASE_KEY_PASSWORD")
        }
    }
    buildTypes {
        getByName("debug") {
            signingConfig = signingConfigs.getByName("release")
            isDebuggable = true
            isMinifyEnabled = false
        }
        getByName("release") {
            signingConfig = signingConfigs.getByName("release")
            isMinifyEnabled = true
            isDebuggable = false
        }
    }
    java {
        toolchain {
            languageVersion.set(JavaLanguageVersion.of(libs.versions.javaLanguage.get()))
        }
    }
    kotlin {
        jvmToolchain {
            languageVersion.set(JavaLanguageVersion.of(libs.versions.javaLanguage.get()))
        }
    }
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
    dependsOn("assembleRelease")
    from("build/outputs/apk/release")
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
