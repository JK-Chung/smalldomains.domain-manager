variable "environment" {
  type = string

  validation {
    condition     = contains(["dev", "stage", "prod"], var.environment)
    error_message = "Valid values for var.environment are [\"dev\",\"stage\",\"prod\"]."
  }
}