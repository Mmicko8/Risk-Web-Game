provider "google" {
    project = var.project
    region = var.region
}


terraform {
  backend "gcs" {
    bucket = "riskybusinessip2-tf-state-staging"
    prefix = "terraform/state"
  }
  required_providers {
    google = {
        source = "hashicorp/google"
        version = "~> 4.0"
    }
  }
}