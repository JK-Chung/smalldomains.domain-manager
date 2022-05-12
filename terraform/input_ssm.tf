data "aws_ssm_parameter" "ecr_repo_url" {
  name = "/ecr/deployment-artifacts/smalldomains/domain-manager"
}

data "aws_ssm_parameter" "ecs-ec2-cluster-name" {
  name = "/ecs/ec2-cluster"
}

data "aws_ssm_parameter" "ecs-instance-role-arn" {
  name = "/iam/ecs/ecs-container-agent-role"
}

data "aws_ssm_parameter" "latest-docker-tag" {
  name = "/smalldomains/domain-manager/latest-docker-tag"
}