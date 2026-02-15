import java.util.Properties
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
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

    compileOptions {
        sourceCompatibility = JavaVersion.toVersion(libs.versions.javaLanguage.get())
        targetCompatibility = JavaVersion.toVersion(libs.versions.javaLanguage.get())
    }

    kotlin {
        compilerOptions {
            jvmTarget.set(JvmTarget.fromTarget(libs.versions.javaLanguage.get()))
        }
    }
}

dependencies {
    implementation(projects.composeApp)
    implementation(libs.sqldelight.android.driver)
    implementation(compose.preview)
    implementation(libs.androidx.activity.compose)
    // implementation(compose.activity)
    implementation(libs.compose.ui)

}

