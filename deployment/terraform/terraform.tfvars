#global settings
project = "ip2-testomgeving"
region = "europe-west1"
location = "europe-west1-b"

#bucket

terraform_staging_bucket_name = "ip2-testenv-tf-state-staging"


# network settings

google_compute_network_name = "main"
google_private_address_name = "private-ip-address"
google_external_address_name = "ingress-ipv4"
google_ip_range_pods = "k8s-pod-range"
google_ip_range_services = "k8s-service-range"
google_subnet_name = "private"
google_router_name = "private"
google_router_nat_name = "nat"
google_compute_nat_address_name = "nat"

# database settings

google_sql_instance_name = "ip2-testenv-privatedb"
google_sql_database_name = "ip2-testenv-database"
google_sql_username = "root-database"
google_database_tier = "db-f1-micro"
google_database_version = "MYSQL_8_0"



# kubernetes settings

kubernetes_namespace = "default"
google_cluster_name = "primary"
google_node_name = "ip2-node-pool"
kubernetes_compute_type = "g1-small"
