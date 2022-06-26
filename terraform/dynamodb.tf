resource "aws_dynamodb_table" "small-domain-redirects" {
  name = "small-domain.redirects"

  billing_mode   = "PROVISIONED"
  read_capacity  = 1
  write_capacity = 1

  hash_key = "small-domain"

  attribute {
    name = "small-domain"
    type = "S"
  }

  server_side_encryption {
    enabled = true
    // use the default DynamoDB KMS key alias/aws/dynamodb
  }

  point_in_time_recovery {
    enabled = var.environment == "prod"
  }

}