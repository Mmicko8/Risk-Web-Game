resource "random_id" "db_name_suffix" {
  byte_length = 4
}

resource "google_sql_database_instance" "ip2-testenv" {
  provider = google-beta
  project = var.project

  name = "${var.google_sql_instance_name}-${random_id.db_name_suffix.hex}"
  region = var.region
  database_version = var.google_database_version
  deletion_protection = false

  depends_on = [google_service_networking_connection.private_vpc_connection]

  settings {
    tier = var.google_database_tier
    ip_configuration {
        ipv4_enabled = false
        private_network = google_compute_network.main.id
    }
  }
}

#team1
resource "google_sql_database" "ip2-testenv-database-team1" {
  name = "${var.google_sql_database_name}-team1"
  instance = google_sql_database_instance.ip2-testenv.name
}

#team2
resource "google_sql_database" "ip2-testenv-database-team2" {
  name = "${var.google_sql_database_name}-team2"
  instance = google_sql_database_instance.ip2-testenv.name
}

#team3
resource "google_sql_database" "ip2-testenv-database-team3" {
  name = "${var.google_sql_database_name}-team3"
  instance = google_sql_database_instance.ip2-testenv.name
}


resource "google_sql_user" "users" {
  name = var.google_sql_username
  instance = google_sql_database_instance.ip2-testenv.name
  host = "%"
  password = "fkU8EKjU4j"
}