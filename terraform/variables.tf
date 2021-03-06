variable "gcp_project" {
  description = "The GCP project id"
}

variable "kube_namespace" {
  description = "The Kubernetes namespace"
}

variable "labels" {
  description = "Labels used in all resources"
  type        = map(string)
  default = {
    manager = "terraform"
    team    = "ror"
    slack   = "talk-ror"
    app     = "uttu"
  }
}

variable "cloudsql_project" {
  description = "GCP project of sql database"
}

variable "service_account_cloudsql_role" {
  description = "cloudsql client role"
  default     = "roles/cloudsql.client"
}

variable "service_account_pubsub_role" {
  description = "pubsub editor role"
  default     = "roles/pubsub.editor"
}

variable "pubsub_topic" {
  description = "pubsub topic for publishing export notifications"
  default     = "ChouetteMergeWithFlexibleLinesQueue"
}

variable "pubsub_project" {
  description = "GCP project of pubsub topic"
}

variable "service_account_storage_role" {
  description = "storage objects create role"
  default     = "roles/storage.objectAdmin"
}

variable "storage_bucket_name" {
  description = "name of storage bucket for exports"
  default     = "marduk-exchange"
}

variable "load_config_file" {
  description = "Do not load kube config file"
  default     = false
}

variable "db_tier" {
  description = "Database instance tier"
  default = "db-g1-small"
}

variable "db_backup_enabled" {
  description = "Enabled automated db backup"
  default = false
}

variable entur_env {
  description = "Whether deployment is to new entur environments"
  default = false
}

variable ror-uttu-db-password {
  description = "Uttu database password"
}
