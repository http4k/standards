import com.vanniktech.maven.publish.MavenPublishBaseExtension
import java.net.URI

plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.version.catalog.update)
    alias(libs.plugins.typeflows)
    alias(libs.plugins.vannitech)
    `java-library`
    signing
    `maven-publish`
}

repositories {
    mavenCentral()
}

configure<MavenPublishBaseExtension> {
    configure<PublishingExtension> {
        val enableSigning = project.findProperty("sign") == "true"

        if (enableSigning) {
            apply(plugin = "signing")
            signing {
                val signingKey: String? by project
                val signingPassword: String? by project
                useInMemoryPgpKeys(signingKey, signingPassword)
                sign(project.the<PublishingExtension>().publications)
            }
        }

        publishToMavenCentral(automaticRelease = false)

        coordinates(
            "org.http4k",
            project.name,
            project.properties["releaseVersion"]?.toString() ?: "LOCAL"
        )

        pom {
            withXml {
                asNode().appendNode("name", project.name)
                asNode().appendNode("description", project.description)
                asNode().appendNode("url", "https://http4k.org")
                asNode().appendNode("developers").apply {
                    appendNode("developer").appendNode("name", "David Denton").parent()
                        .appendNode("email", "david@http4k.org")
                    appendNode("developer").appendNode("name", "Ivan Sanchez").parent()
                        .appendNode("email", "ivan@http4k.org")
                }
                asNode().appendNode("scm")
                    .appendNode("url", "https://github.com/http4k/standards").parent()
                    .appendNode("connection", "scm:git:git@github.com:http4k/standards.git").parent()
                    .appendNode("developerConnection", "scm:git:git@github.com:http4k/standards.git")

                asNode().appendNode("licenses").appendNode("license")
                    .appendNode("name", "Apache-2.0").parent()
                    .appendNode("url", "http://http4k.org/commercial-license")
            }
        }
    }
}

dependencies {
    api(libs.typeflows.github)
    api(libs.typeflows.github.marketplace)
    api(libs.typeflows.github.project.standards)

    typeflowsApi(project(":"))
}

