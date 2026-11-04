# CIA2 Azure CI/CD demo

Spring Boot task board deployed to Azure Kubernetes Service (AKS) by GitHub Actions. A push to `master` builds and tests the Maven project, builds a Docker image tagged with the commit SHA, pushes it to Azure Container Registry (ACR), and updates the Kubernetes Deployment.

## Azure resources

- Resource group: `cia2-azure-rg`
- ACR: `cia2taskboard9472` (East Asia)
- Planned AKS cluster: `cia2-aks-demo` (East Asia, Free control-plane tier, one Linux node)

The AKS node, registry, disk, and public load balancer can incur charges. The Azure budget is an alert and does not stop resources automatically. Review Cost Management and remove the resource group after the demonstration.

## GitHub Actions configuration

In the repository's **Settings → Secrets and variables → Actions**, add these repository variables:

| Variable | Value |
| --- | --- |
| `ACR_NAME` | `cia2taskboard9472` |
| `AZURE_RESOURCE_GROUP` | `cia2-azure-rg` |
| `AKS_CLUSTER_NAME` | `cia2-aks-demo` |

Add these **secrets** after creating a Microsoft Entra application/service principal and its GitHub Actions federated credential:

| Secret | Value |
| --- | --- |
| `AZURE_CLIENT_ID` | Application (client) ID |
| `AZURE_TENANT_ID` | Directory (tenant) ID |
| `AZURE_SUBSCRIPTION_ID` | Azure for Students subscription ID |

Grant that identity `AcrPush` on the registry and the AKS Cluster User and AKS RBAC Writer roles on the cluster. Configure the federated credential for this repository and the `master` branch. Do not put credentials in source files.

## Pipeline and demo

1. Push a commit to `master`; GitHub Actions runs `.github/workflows/ci-cd.yml`.
2. Confirm the Maven build and test steps pass, and the image appears in ACR with the commit SHA tag.
3. Confirm the AKS rollout is ready in the workflow log. The Kubernetes Service exposes the app on port 80; wait for its external IP, then open `http://<EXTERNAL-IP>`.
4. Change visible text in `src/main/resources/static/index.html`, commit and push, then show the succeeding Actions run and updated page.

For local inspection, `./mvnw spring-boot:run` serves the application at `http://localhost:8080`.
