resource "aws_iam_role" "task_role_domain_manager" {
  name = "TaskRoleForSmallDomainsDomainManager"

  managed_policy_arns = [
    # An AWS-Managed Policy designed that gives necessary authorisations to the ECR
    "arn:aws:iam::aws:policy/service-role/AmazonECSTaskExecutionRolePolicy"
  ]

  inline_policy {
    name = "RO_Operations_DynamoDB"

    policy = jsonencode({
      Version = "2012-10-17"
      Statement = [
        {
          Action   = ["ec2:Describe*"]
          Effect   = "Allow"
          Resource = "*"
        },
      ]
    })
  }

  assume_role_policy = jsonencode({
    "Version" : "2008-10-17",
    "Statement" : [
      {
        "Sid" : "",
        "Effect" : "Allow",
        "Principal" : {
          "Service" : "ecs-tasks.amazonaws.com"
        },
        "Action" : "sts:AssumeRole"
      }
    ]
  })
}

resource "aws_iam_role_policy" "rw_dynamodb" {
  name = "RW_DynamoDB_SmallDomainRedirects"
  role = aws_iam_role.task_role_domain_manager.id

  policy = jsonencode({
    Version = "2012-10-17"
    Statement = [
      {
        Action = [
          "dynamodb:DescribeContributorInsights",
          "dynamodb:RestoreTableToPointInTime",
          "dynamodb:UpdateGlobalTable",
          "dynamodb:DeleteTable",
          "dynamodb:UpdateTableReplicaAutoScaling",
          "dynamodb:DescribeTable",
          "dynamodb:PartiQLInsert",
          "dynamodb:GetItem",
          "dynamodb:DescribeContinuousBackups",
          "dynamodb:DescribeExport",
          "dynamodb:EnableKinesisStreamingDestination",
          "dynamodb:BatchGetItem",
          "dynamodb:DisableKinesisStreamingDestination",
          "dynamodb:UpdateTimeToLive",
          "dynamodb:BatchWriteItem",
          "dynamodb:PutItem",
          "dynamodb:PartiQLUpdate",
          "dynamodb:Scan",
          "dynamodb:StartAwsBackupJob",
          "dynamodb:UpdateItem",
          "dynamodb:UpdateGlobalTableSettings",
          "dynamodb:CreateTable",
          "dynamodb:RestoreTableFromAwsBackup",
          "dynamodb:GetShardIterator",
          "dynamodb:DescribeReservedCapacity",
          "dynamodb:ExportTableToPointInTime",
          "dynamodb:DescribeBackup",
          "dynamodb:UpdateTable",
          "dynamodb:GetRecords",
          "dynamodb:DescribeTableReplicaAutoScaling",
          "dynamodb:DeleteItem",
          "dynamodb:PurchaseReservedCapacityOfferings",
          "dynamodb:CreateTableReplica",
          "dynamodb:ListTagsOfResource",
          "dynamodb:UpdateContributorInsights",
          "dynamodb:CreateBackup",
          "dynamodb:UpdateContinuousBackups",
          "dynamodb:DescribeReservedCapacityOfferings",
          "dynamodb:PartiQLSelect",
          "dynamodb:CreateGlobalTable",
          "dynamodb:DescribeKinesisStreamingDestination",
          "dynamodb:DescribeLimits",
          "dynamodb:ConditionCheckItem",
          "dynamodb:Query",
          "dynamodb:DescribeStream",
          "dynamodb:DeleteTableReplica",
          "dynamodb:DescribeTimeToLive",
          "dynamodb:ListStreams",
          "dynamodb:DescribeGlobalTableSettings",
          "dynamodb:DescribeGlobalTable",
          "dynamodb:RestoreTableFromBackup",
          "dynamodb:DeleteBackup",
          "dynamodb:PartiQLDelete"
        ]
        Effect   = "Allow"
        Resource = aws_dynamodb_table.small-domain-redirects.arn
      },
    ]
  })
}