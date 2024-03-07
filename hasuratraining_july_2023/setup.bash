#! /usr/bin/bash

# Delete images and containers.

docker system prune -af

# Delete volumes.

docker system prune --volumes -f

# Run a local registry.

docker run -d -p 5000:5000 --restart=always --name registry registry:2

# Pull images from public registry.

docker pull postgres:14
docker pull hasura/graphql-engine:v2.12.0-beta.1

# Create an additional tags associated with the local registry.

docker tag postgres:14 localhost:5000/my-postgres
docker tag hasura/graphql-engine:v2.12.0-beta.1 localhost:5000/my-hasura

# Push the images to the local registry.

docker push localhost:5000/my-postgres
docker push localhost:5000/my-hasura

# Remove the locally-cached PostgreSQL images.  Note that this does NOT remove the image from the local registry.

docker image remove hasura/graphql-engine:v2.12.0-beta.1
docker image remove postgres:14
docker image remove localhost:5000/my-hasura
docker image remove localhost:5000/my-postgres

# Pull images from the local registry.

docker pull localhost:5000/my-postgres
docker pull localhost:5000/my-hasura
