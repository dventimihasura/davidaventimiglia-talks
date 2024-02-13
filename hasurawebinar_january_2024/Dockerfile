FROM node:latest
WORKDIR /root
RUN npm i @graphql-mesh/cli graphql
RUN npm i @graphql-mesh/graphql
RUN npm i @graphql-mesh/transform-rename
RUN npm i @graphql-mesh/transform-federation
CMD npx mesh dev
