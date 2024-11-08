plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.google.android.libraries.mapsplatform.secrets.gradle.plugin)
    id("com.google.devtools.ksp")
    id("kotlin-parcelize")
}

android {
    namespace = "com.reza.storyapp"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.reza.storyapp"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        buildConfigField("String", "BASE_URL", "\"https://story-api.dicoding.dev/v1/\"")
    }

    buildFeatures {
        viewBinding = true
        buildConfig = true
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
        freeCompilerArgs += ("-Xopt-in=kotlin.RequiresOptIn")
    }

    testOptions {
        animationsDisabled = true
        unitTests.isReturnDefaultValues = true
    }
}

dependencies {
    implementation(libs.glide)
    implementation(libs.retrofit)
    implementation(libs.logging.interceptor)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.play.services.maps)
    testImplementation(libs.junit.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)

    implementation(libs.androidx.datastore.preferences)
    implementation(libs.androidx.lifecycle.viewmodel.ktx)
    implementation(libs.androidx.lifecycle.livedata.ktx)
    implementation(libs.androidx.activity.ktx)
    implementation(libs.retrofit2.converter.gson)
    implementation(libs.androidx.exifinterface)

    implementation(libs.androidx.paging.runtime.ktx)

    implementation(libs.androidx.room.ktx)
    implementation(libs.androidx.room.runtime)
    ksp(libs.room.compiler)

    androidTestImplementation(libs.androidx.core.testing) //InstantTaskExecutorRule
    androidTestImplementation(libs.kotlinx.coroutines.test) //TestDispatcher

    testImplementation(libs.androidx.core.testing) // InstantTaskExecutorRule
    testImplementation(libs.kotlinx.coroutines.test) //TestDispatcher
    testImplementation(libs.mockito.core)
    testImplementation(libs.mockito.inline)
    implementation(libs.androidx.room.paging)
    implementation(libs.play.services.location)

    //TestCoroutineDispatcher
    debugImplementation("androidx.fragment:fragment-testing:1.5.5") {
        exclude(group = "androidx.test", module = "core")
    }

    androidTestImplementation(libs.espresso.intents) //IntentsTestRule

    implementation(libs.androidx.espresso.idling.resource)
}