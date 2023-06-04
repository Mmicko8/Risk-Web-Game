resource "google_project_service" "compute" {
  service = "compute.googleapis.com"
}

resource "google_project_service" "container" {
  service = "container.googleapis.com"
}

resource "google_project_service" "servicenetworking" {
  service = "servicenetworking.googleapis.com"
}

resource "google_compute_network" "main" {
  name = var.google_compute_network_name
  routing_mode = "REGIONAL"
  auto_create_subnetworks = false
  mtu = 1460
  delete_default_routes_on_create = false

  depends_on = [
    google_project_service.compute,
    google_project_service.container
  ]
}

resource "google_compute_global_address" "private_ip_address" {
  provider = google-beta
  project = var.project

  name = var.google_private_address_name
  purpose = "VPC_PEERING"
  address_type = "INTERNAL"
  prefix_length = 16
  network = google_compute_network.main.id

}

resource "google_service_networking_connection" "private_vpc_connection" {
  provider = google-beta

  network = google_compute_network.main.id
  service = "servicenetworking.googleapis.com"
  reserved_peering_ranges = [google_compute_global_address.private_ip_address.name]

  depends_on = [
    google_project_service.servicenetworking
  ]
}

resource "google_compute_global_address" "external_ip_address" {
  name = var.google_external_address_name
  address_type = "EXTERNAL"

  depends_on = [
    google_project_service.compute
  ]
}