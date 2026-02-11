plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    id("kotlin-kapt")
}

android {
    namespace = "com.example.app_orm_jorge_diaz_diego_diaz_pablo_mata_rodrigo_herrero_examen_t2"
    compileSdk = 34

    defaultConfig {
        applicationId =
            "com.example.app_orm_jorge_diaz_diego_diaz_pablo_mata_rodrigo_herrero_examen_t2"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
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
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }
    buildFeatures {
        viewBinding = true
    }
}

dependencies {
    implementation(libs.androidx.room.runtime)
    implementation(libs.androidx.room.ktx)
    kapt(libs.androidx.room.compiler)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)

    // Libreria: https://github.com/sparrow007/CarouselRecyclerview
    implementation("com.github.sparrow007:carouselrecyclerview:1.2.6")

    // Glide
    implementation("com.github.bumptech.glide:glide:4.15.1")

    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)


    // Retrofit: La librería para hacer peticiones HTTP (como libcurl en C++)
    implementation(libs.retrofit.core)

    // Gson: Para convertir los JSON de MongoDB a objetos de Kotlin automáticamente
    implementation(libs.retrofit.gson)

    // Corrutinas: Para que la app no se congele al consultar los datos
    implementation(libs.kotlinx.coroutines)

    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.6.2")
}