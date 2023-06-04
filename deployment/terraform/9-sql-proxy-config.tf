#data for config
data "google_client_config" "provider" {

}

#basic k8s config
provider "kubernetes" {
  cluster_ca_certificate = base64decode(google_container_cluster.primary.master_auth.0.cluster_ca_certificate)
  host = google_container_cluster.primary.endpoint
  token = data.google_client_config.provider.access_token
}


# Create a Google service account
resource "google_service_account" "ip-testenv-gsa-account" {
  account_id = "ip-testenv-gsa-account"
  display_name = "ip-testenv-gsa-account"
  project = var.project
}

# Grant the Google service account the cloudsql.client role
resource "google_project_iam_member" "ip-testenv-gsa-account" {
  project = var.project
  role = "roles/cloudsql.client"
  member = "serviceAccount:${google_service_account.ip-testenv-gsa-account.email}"
}

# Create a Kubernetes service account
resource "kubernetes_service_account" "ip-testenv-ksa-account" {
  depends_on = [
    google_project_iam_member.ip-testenv-gsa-account
  ]
  metadata {
    name = "ip-testenv-ksa-account"
    namespace = var.kubernetes_namespace
    annotations = {
      "iam.gke.io/gcp-service-account" = google_service_account.ip-testenv-gsa-account.email
    }
  }
}

# Bind the Google service account to the Kubernetes service account
resource "google_service_account_iam_binding" "ip-testenv-gsa-account" {
  depends_on = [
    kubernetes_service_account.ip-testenv-ksa-account
  ]
  service_account_id = google_service_account.ip-testenv-gsa-account.id
  role = "roles/iam.workloadIdentityUser"
  members = [
    "serviceAccount:${var.project}.svc.id.goog[${var.kubernetes_namespace}/${kubernetes_service_account.ip-testenv-ksa-account.metadata[0].name}]"
  ]
}
