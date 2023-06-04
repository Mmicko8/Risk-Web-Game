resource "google_compute_subnetwork" "private" {
  name = var.google_subnet_name
  ip_cidr_range = "10.0.0.0/18"
  region = var.region
  network = google_compute_network.main.id
  private_ip_google_access = true

  secondary_ip_range {
    range_name = var.google_ip_range_pods
    ip_cidr_range = "10.48.0.0/14"
  }

  secondary_ip_range {
    range_name = var.google_ip_range_services
    ip_cidr_range = "10.52.0.0/20"
  }
}