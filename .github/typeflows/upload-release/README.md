# Publish Artifacts (upload-release.yml)

```mermaid
%%{init: {"flowchart": {"curve": "basis"}}}%%
flowchart TD
    repositorydispatch(["🔔 repository_dispatch<br/>(release)"])
    subgraph uploadreleaseyml["Publish Artifacts"]
        uploadreleaseyml_release["Release<br/>🐧 ubuntu-latest"]
    end
    repositorydispatch --> uploadreleaseyml_release
```

## Job: Release

| Job | OS | Dependencies | Config |
|-----|----|--------------|---------| 
| `release` | 🐧 ubuntu-latest | - | - |

### Steps

```mermaid
%%{init: {"flowchart": {"curve": "basis"}}}%%
flowchart TD
    step1["Step 1: Checkout"]
    style step1 fill:#f8f9fa,stroke:#495057
    action1["🎬 actions<br/>checkout<br/><br/>📝 Inputs:<br/>• ref: ${{ github.event.client_payloa..."]
    style action1 fill:#e1f5fe,stroke:#0277bd
    step1 -.-> action1
    step2["Step 2: Setup Java"]
    style step2 fill:#f8f9fa,stroke:#495057
    action2["🎬 actions<br/>setup-java<br/><br/>📝 Inputs:<br/>• java-version: 21<br/>• distribution: adopt"]
    style action2 fill:#e1f5fe,stroke:#0277bd
    step2 -.-> action2
    step1 --> step2
    step3["Step 3: Setup Gradle"]
    style step3 fill:#f8f9fa,stroke:#495057
    action3["🎬 gradle<br/>actions/setup-gradle"]
    style action3 fill:#e1f5fe,stroke:#0277bd
    step3 -.-> action3
    step2 --> step3
    step4["Step 4: Publish<br/>💻 bash"]
    style step4 fill:#f3e5f5,stroke:#7b1fa2
    step3 --> step4
    step5["Step 5: Build release note<br/>💻 bash"]
    style step5 fill:#f3e5f5,stroke:#7b1fa2
    step4 --> step5
    step6["Step 6: Create Release"]
    style step6 fill:#f8f9fa,stroke:#495057
    action6["🎬 actions<br/>create-release<br/><br/>📝 Inputs:<br/>• tag_name: ${{ github.event.client_payloa...<br/>• release_name: ${{ github.event.client_payloa...<br/>• body_path: NOTE.md<br/>• draft: false<br/>• prerelease: false"]
    style action6 fill:#e1f5fe,stroke:#0277bd
    step6 -.-> action6
    step5 --> step6
```

**Step Types Legend:**
- 🔘 **Step Nodes** (Gray): Workflow step execution
- 🔵 **Action Blocks** (Blue): External GitHub Actions
- 🔷 **Action Blocks** (Light Blue): Local repository actions
- 🟣 **Script Nodes** (Purple): Run commands/scripts
- **Solid arrows** (→): Step execution flow
- **Dotted arrows** (-.->): Action usage with inputs