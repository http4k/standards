plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.version.catalog.update)
    alias(libs.plugins.typeflows)
}

repositories {
    mavenCentral()
    mavenLocal()
}

dependencies {
    api(libs.typeflows.github)
    api(libs.typeflows.github.marketplace)
    api(libs.typeflows.github.project.standards)

    typeflowsApi(project(":"))
}

