locals {
  small-domain-domain-manager-container-name = "small-domains--domain-manager"
  small-domain-domain-manager-exposed-port   = 8080
}

resource "aws_ecs_service" "domain-manager" {
  name            = "smalldomains--domain-manager"
  launch_type     = "EC2"
  cluster         = data.aws_ssm_parameter.ecs-ec2-cluster-arn.value
  task_definition = aws_ecs_task_definition.domain-manager.family

  desired_count                      = var.environment == "dev" ? 1 : 2
  deployment_minimum_healthy_percent = 100
  deployment_maximum_percent         = 200

  ordered_placement_strategy {
    type  = "spread"
    field = "attribute:ecs.availability-zone"
  }

  load_balancer {
    target_group_arn = data.aws_ssm_parameter.target-group-arn.value
    container_name   = local.small-domain-domain-manager-container-name
    container_port   = local.small-domain-domain-manager-exposed-port
  }

  network_configuration {
    subnets         = split(",", data.aws_ssm_parameter.public_subnet_ids.value)
    security_groups = [data.aws_ssm_parameter.sg_for_ecs_services.value]
  }

  health_check_grace_period_seconds = 30
}

resource "aws_ecs_task_definition" "domain-manager" {
  family                   = "smalldomains--domain-manager"
  requires_compatibilities = ["EC2"]
  network_mode             = "awsvpc"
  cpu                      = 1024
  memory                   = 256
  task_role_arn            = aws_iam_role.task_role_domain_manager.arn
  execution_role_arn       = data.aws_ssm_parameter.ecs-instance-role-arn.value

  container_definitions = jsonencode([
    {
      name      = local.small-domain-domain-manager-container-name
      image     = format("%s:%s", data.aws_ssm_parameter.ecr_repo_url.value, data.aws_ssm_parameter.latest-docker-tag.value)
      essential = true
      portMappings = [
        {
          containerPort = local.small-domain-domain-manager-exposed-port
        }
      ]
      logConfiguration = {
        logDriver = "awslogs"
        options = {
          awslogs-group         = aws_cloudwatch_log_group.domainmanager.name
          awslogs-region        = data.aws_region.current.name
          awslogs-stream-prefix = "domain-manager"
        }
      }
    }
  ])
}

resource "aws_cloudwatch_log_group" "domainmanager" {
  name = "small-domains--domain-manager"
}