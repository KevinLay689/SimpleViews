plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
    id("maven-publish")
}

android {
    namespace = "com.github.simpleviews"
    compileSdk = 36

    defaultConfig {
        minSdk = 23
        consumerProguardFiles("consumer-rules.pro")
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
}

kotlin {
    compilerOptions {
        jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_17)
    }
}

dependencies {
    // Image loading. Exposed as `api` so consumers can call Coil directly too.
    api("io.coil-kt.coil3:coil:3.2.0")
    api("io.coil-kt.coil3:coil-network-okhttp:3.2.0")

    // All androidx deps are `api`: our views publicly extend AppCompat*,
    // RecyclerView and SwipeRefreshLayout, so consumers need the types.
    api("androidx.appcompat:appcompat:1.7.0")
    api("androidx.core:core-ktx:1.15.0")
    api("androidx.recyclerview:recyclerview:1.3.2")
    api("androidx.swiperefreshlayout:swiperefreshlayout:1.1.0")
}

// JitPack builds com.github.<user>:SimpleViews:<tag> from this publication.
// JitPack overrides groupId/artifactId/version with the repo coordinates;
// change the groupId below to your GitHub username for clarity.
afterEvaluate {
    publishing {
        publications {
            create<MavenPublication>("release") {
                from(components["release"])
                groupId = "com.github.your-github-username"
                artifactId = "SimpleViews"
                version = "1.0.0"
            }
        }
    }
}
