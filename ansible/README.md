# Ansible Deployment Automation

This directory contains Ansible configuration management and deployment automation assets for the Java task board application on Kubernetes.

## Files

- `inventory.ini`: Inventory configuration specifying target host environments (local/bastion/runner).
- `deploy-k8s.yml`: Playbook automating:
  1. Pre-flight verification of `kubectl` and cluster connectivity.
  2. Dynamic manifest parameterization (injecting the versioned container image tag into `k8s/deployment.yaml`).
  3. Declarative manifest application to the target Kubernetes cluster (`kubectl apply`).
  4. Monitoring rolling update progress until rollout status is ready.
  5. Fetching and asserting running status of Pods and LoadBalancer Service.

## Execution

Ensure `kubectl` is configured with valid AKS context (via `az aks get-credentials` and `kubelogin`), then run:

```bash
ansible-playbook -i ansible/inventory.ini ansible/deploy-k8s.yml
```

Or pass a specific Docker image tag:

```bash
IMAGE=cia2taskboard9472.azurecr.io/cia2-taskboard:<COMMIT_SHA> ansible-playbook -i ansible/inventory.ini ansible/deploy-k8s.yml
```
