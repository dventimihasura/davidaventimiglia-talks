# What #

This repository demonstrates these [Hasura](https://hasura.io/)
[federation architectures](https://hasura.io/docs/latest/data-federation/overview/)
all-up in one stack.

  * [multi-protocal](https://hasura.io/docs/latest/data-federation/hasura-graphql-federation-architectures/#hasura-multi-protocol-data-federation)
  * [REST gateway](https://hasura.io/docs/latest/data-federation/hasura-graphql-federation-architectures/#hasura-as-a-graphql-gateway-to-rest-services-or-microservices)
  * [supergraph](https://hasura.io/docs/latest/data-federation/hasura-graphql-federation-architectures/#hasura-as-a-federated-supergraph-gateway)

# Why #

This is a useful demonstration for [HasuraCon 2023](https://hasura.io/events/hasura-con-2023) and beyond.

# How #

It uses Docker and Docker Compose to set up these services.

  * **metadatadb** :: PostgreSQL cluster with multiple Hasura metadata databases
  * **catalogdb** :: PostgreSQL cluster with a single database for the `product` table
  * **marketplacedb** :: PostgreSQL cluster with a single database for the `account`, `order`, `order_detail`, `region` tables
  * **postgrest** :: PostgREST server to host **catalogdb** as an OpenAPI microservice
  * **restapi** :: nginx reverse proxy to **postgrest**
  * **swagger** :: Swagger UI web server for **restapi**
  * **subgraph1** :: Hasura server for **marketplacedb** using direct DB connection
  * **subgraph2** :: Hasura server for **restapi** using remote Actions
  * **supergraph** :: Hasura server composing **subgraph1** and **subgraph2** using Remote schema relationships
  
The data model comprising **marketplacedb** and **catalogdb** with
their `account`, `order`, `order_detail`, and `region` tables is that
of a grocery store wholesale distrubutor operating in geographic sales
regions within the United States.

# Steps #

Get the code.

```bash
git clone https://github.com/dventimihasura/davidaventimiglia-talks.git
cd hasuracon_july_2023
```

Create a `.env` file with available port numbers on your host.  Edit
as needed.

```bash
cat > .env << EOL
CATALOGDB_PORT=5434
MARKETPLACEDB_PORT=5435
POSTGREST_PORT=8081
RESTAPI_PORT=8082
SUBGRAPH1_PORT=8084
SUBGRAPH2_PORT=8085
SUPERGRAPH_PORT=8086
SWAGGER_PORT=8083
EOL
```

Launch the services.

```bash
docker compose up -d
```

Visit these addresses in a browser to continue the configuration.

  * http://localhost:8082 :: OpenAPI endpoint for **catalogdb** data
  * http://localhost:8083 :: Swagger UI for **catalogdb** data (for reference purposes)
  * http://localhost:8084 :: Hasura subgraph1 for **marketplacedb** data using direct DB connection
  * http://localhost:8085 :: Hasura subgraph2 for **catalogdb** data using Remote Actions
  * http://localhost:8086 :: Hasura supergraph composing **subgraph1** and **subgraph2**
  
On Linux this can be done from the command line in the following way.

```bash
xdg-open http://localhost:8081 # OpenAPI endpoint
xdg-open http://localhost:8082 # Swagger UI
xdg-open http://localhost:8083 # Hasura subgraph1 (marketplace)
xdg-open http://localhost:8084 # Hasura subgraph2 (catalog)
xdg-open http://localhost:8085 # Hasura supergraph
```

On Mac this can be done from the command line in a slightly different way.

```bash
open http://localhost:8081 # OpenAPI endpoint
open http://localhost:8082 # Swagger UI
open http://localhost:8083 # Hasura subgraph1 (marketplace)
open http://localhost:8084 # Hasura subgraph2 (catalog)
open http://localhost:8085 # Hasura supergraph
```
