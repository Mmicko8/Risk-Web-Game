#global settings

variable "project" {
  type = string
}

variable "region" {
  type = string
}

variable "location" {
  type = string
}

#bucket

variable "terraform_staging_bucket_name" {
  type = string
}


# network settings


variable "google_compute_network_name" {
  type = string
}

variable "google_private_address_name" {
  type = string
}

variable "google_external_address_name" {
  type = string
}

variable "google_ip_range_pods" {
  type = string
}

variable "google_ip_range_services" {
  type = string
}

variable "google_subnet_name" {
  type = string
}

variable "google_router_name" {
  type = string
}

variable "google_router_nat_name" {
  type = string
}

variable "google_compute_nat_address_name" {
  type = string
}


# database settings

variable "google_sql_instance_name" {
  type = string
}

variable "google_sql_database_name" {
  type = string
}

variable "google_sql_username" {
  type = string
}

variable "google_database_tier" {
  type = string
}

variable "google_database_version" {
  type = string
}


# kubernetes settings


variable "kubernetes_namespace" {
  type = string
}

variable "google_cluster_name" {
  type = string
}

variable "google_node_name" {
  type = string
}

variable "kubernetes_compute_type" {
  type = string
}
