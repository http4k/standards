import io.typeflows.github.workflows.Job
import io.typeflows.github.workflows.Permission.Contents
import io.typeflows.github.workflows.PermissionLevel.Write
import io.typeflows.github.workflows.Permissions
import io.typeflows.github.workflows.RunsOn.Companion.UBUNTU_LATEST
import io.typeflows.github.workflows.Secrets
import io.typeflows.github.workflows.StrExp
import io.typeflows.github.workflows.Workflow
import io.typeflows.github.workflows.steps.RunCommand
import io.typeflows.github.workflows.steps.RunScript
import io.typeflows.github.workflows.steps.SendRepositoryDispatch
import io.typeflows.github.workflows.steps.marketplace.Checkout
import io.typeflows.github.workflows.steps.marketplace.JavaDistribution.Adopt
import io.typeflows.github.workflows.steps.marketplace.JavaVersion.V21
import io.typeflows.github.workflows.steps.marketplace.SetupGradle
import io.typeflows.github.workflows.steps.marketplace.SetupJava
import io.typeflows.github.workflows.triggers.Branches
import io.typeflows.github.workflows.triggers.Paths
import io.typeflows.github.workflows.triggers.PullRequest
import io.typeflows.github.workflows.triggers.Push
import io.typeflows.github.workflows.triggers.WorkflowDispatch
import io.typeflows.util.Builder

class Build : Builder<Workflow> {
    override fun build() = Workflow("build") {
        displayName = "Build in CI"
        on += WorkflowDispatch()

        permissions = Permissions(Contents to Write)

        on += Push {
            branches = Branches.Ignore("develop")
            paths = Paths.Ignore("**/.md")
        }

        on += PullRequest {
            paths = Paths.Ignore("**/.md")
        }

        jobs += Job("build", UBUNTU_LATEST) {
            name = "Build and Test"

            steps += Checkout()

            steps += SetupJava(Adopt, V21)

            steps += SetupGradle()

            steps += RunCommand("./gradlew check --info", "Build")

            val token = Secrets.string("WORKFLOWS_TOKEN")

            steps += RunScript("scripts/tag-if-required.sh", "Release (if required)") {
                id = "get-version"
                condition = StrExp.of("github.ref").isEqualTo("refs/heads/main")
                env["GH_TOKEN"] = token
            }

            steps += SendRepositoryDispatch(
                "release", token,
                mapOf("tag" to StrExp.of("steps.get-version.outputs.tag").toString())
            ) {
                condition = StrExp.of("github.ref").isEqualTo("refs/heads/main").and(
                    StrExp.of("steps.get-version.outputs.tag-created").isEqualTo("true")
                )
            }
        }
    }
}
