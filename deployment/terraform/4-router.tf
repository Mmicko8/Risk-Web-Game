resource "google_compute_router" "router" {
  name = var.google_router_name
  region = var.region
  network = google_compute_network.main.id
}