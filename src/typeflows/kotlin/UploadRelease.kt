import io.typeflows.github.workflow.Job
import io.typeflows.github.workflow.RunsOn
import io.typeflows.github.workflow.Secrets
import io.typeflows.github.workflow.Workflow
import io.typeflows.github.workflow.step.RunScript
import io.typeflows.github.workflow.step.UseAction
import io.typeflows.github.workflow.step.marketplace.Checkout
import io.typeflows.github.workflow.step.marketplace.JavaDistribution.Adopt
import io.typeflows.github.workflow.step.marketplace.JavaVersion.V21
import io.typeflows.github.workflow.step.marketplace.SetupGradle
import io.typeflows.github.workflow.step.marketplace.SetupJava
import io.typeflows.github.workflow.trigger.RepositoryDispatch
import io.typeflows.util.Builder

class UploadRelease : Builder<Workflow> {
    override fun build() = Workflow.configure("upload-release") { workflow ->
        workflow.displayName = "Publish Artifacts"
        workflow.on += RepositoryDispatch("release")

        workflow.jobs += Job("release", RunsOn.UBUNTU_LATEST) {

            name = "Release"
            steps += Checkout.configure { it ->
                it.with["ref"] = $$"${{ github.event.client_payload.tag }}"
            }

            steps += SetupJava(Adopt, V21)

            steps += SetupGradle()

            steps += RunScript.configure("scripts/publish-artifacts.sh", "Publish") { script ->
                script.env["RELEASE_VERSION"] = $$"${{ github.event.client_payload.tag }}"
                script.env["SIGNING_KEY"] = $$"${{ secrets.SIGNING_KEY }}"
                script.env["SIGNING_PASSWORD"] = $$"${{ secrets.SIGNING_PASSWORD }}"
                script.env["ORG_GRADLE_PROJECT_mavenCentralUsername"] = $$"${{ secrets.MAVEN_CENTRAL_USERNAME }}"
                script.env["ORG_GRADLE_PROJECT_mavenCentralPassword"] = $$"${{ secrets.MAVEN_CENTRAL_PASSWORD }}"
                script.env["ORG_GRADLE_PROJECT_signingInMemoryKey"] = $$"${{ secrets.SIGNING_KEY }}"
                script.env["ORG_GRADLE_PROJECT_signingInMemoryKeyPassword"] = $$"${{ secrets.SIGNING_PASSWORD }}"
            }

            steps += RunScript.configure("scripts/build-release-note.sh", "Build release note") { script ->
                script.env["RELEASE_VERSION"] = $$"${{ github.event.client_payload.tag }}"
            }

            steps += UseAction("actions/create-release@v1", "Create Release") {
                env["GITHUB_TOKEN"] = Secrets.GITHUB_TOKEN
                with["tag_name"] = $$"${{ github.event.client_payload.tag }}"
                with["release_name"] = $$"${{ github.event.client_payload.tag }}"
                with["body_path"] = "NOTE.md"
                with["draft"] = "false"
                with["prerelease"] = "false"
            }
        }
    }
}
