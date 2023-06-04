resource "google_container_cluster" "primary" {
  name = var.google_cluster_name
  location = var.location
  remove_default_node_pool = true
  initial_node_count = 1
  network = google_compute_network.main.self_link
  subnetwork = google_compute_subnetwork.private.self_link
  logging_service = "logging.googleapis.com/kubernetes"
  # monitoring_service = "monitoring.googleapis.com/kubernetes"
  networking_mode = "VPC_NATIVE"
  #note: monitoring_service gaat weggedaan moeten worden wanneer we prempheus gaan installeren

  release_channel {
    channel = "REGULAR"
  }

  workload_identity_config {
    workload_pool = "${var.project}.svc.id.goog"
  }

  ip_allocation_policy {
    cluster_secondary_range_name = var.google_ip_range_pods
    services_secondary_range_name = var.google_ip_range_services
  }

  private_cluster_config {
    enable_private_nodes = true
    enable_private_endpoint = false
    master_ipv4_cidr_block = "172.16.0.0/28"
  }
}

resource "google_container_node_pool" "primary_spots" {
  name = var.google_node_name
  cluster = google_container_cluster.primary.id
  node_count = 2

  node_config {
    preemptible = true
    machine_type = var.kubernetes_compute_type
    disk_size_gb = 25

    oauth_scopes = [ 
        "https://www.googleapis.com/auth/cloud-platform"
     ]
  }

}