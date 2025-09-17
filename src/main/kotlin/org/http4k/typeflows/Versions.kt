package org.http4k.typeflows

import io.typeflows.github.workflows.steps.marketplace.JavaDistribution.Temurin
import io.typeflows.github.workflows.steps.marketplace.JavaVersion.V21

object Versions {
    const val GRADLE = "9.0.0"
    val JDK = Temurin
    val JAVA_VERSION = V21
}
