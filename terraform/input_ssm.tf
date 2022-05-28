data "aws_ssm_parameter" "ecr_repo_url" {
  name = "/ecr/deployment-artifacts/smalldomains/domain-manager"
}

data "aws_ssm_parameter" "ecs-ec2-cluster-arn" {
  name = "/ecs/ec2-cluster"
}

data "aws_ssm_parameter" "ecs-instance-role-arn" {
  name = "/iam/ecs/ecs-container-agent-role"
}

data "aws_ssm_parameter" "latest-docker-tag" {
  name = "/smalldomains/domain-manager/latest-docker-tag"
}

data "aws_ssm_parameter" "target-group-arn" {
  name = "/elb/target-groups/smalldomains/domain-manager"
}

data "aws_ssm_parameter" "sg_for_ecs_services" {
  name = "/sg/ecs-services/allow-load-balancer-ingress-only"
}

data "aws_ssm_parameter" "public_subnet_ids" {
  name = "/vpc/public_subnets"
}

data "aws_ssm_parameter" "vpc_id" {
  name = "/vpc/main"
}