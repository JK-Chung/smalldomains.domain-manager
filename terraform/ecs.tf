#AWS_REGION: MY_AWS_REGION                   # set this to your preferred AWS region, e.g. us-west-1
#ECR_REPOSITORY: MY_ECR_REPOSITORY           # set this to your Amazon ECR repository name
#ECS_SERVICE: MY_ECS_SERVICE                 # set this to your Amazon ECS service name
#ECS_CLUSTER: MY_ECS_CLUSTER                 # set this to your Amazon ECS cluster name
#ECS_TASK_DEFINITION: MY_ECS_TASK_DEFINITION # set this to the path to your Amazon ECS task definition
## file, e.g. .aws/task-definition.json
#CONTAINER_NAME: MY_CONTAINER_NAME           # set this to the name of the container in the
## containerDefinitions section of your task definition

# DEPLOY ECS_TASK_DEFINITION, ECS_SERVICE
# FROM SSM, retrieve, ECS_REPOSITORY, ECS_CLUSTER

resource "aws_ecs_task_definition" "domain-manager" {
  family                = "smalldomains--domain-manager"
  container_definitions = []
}