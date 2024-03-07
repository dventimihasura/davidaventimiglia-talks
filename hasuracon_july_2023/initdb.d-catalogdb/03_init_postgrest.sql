-- -*- sql-product: postgres; -*-

create role web_anon nologin;

create schema api;

grant usage on schema api to web_anon;

create or replace view api.product as select * from "public".product;

grant select on api.product to web_anon;

create or replace function api.root() returns json as $function$
  declare
    openapi json = format(
      $json$  
{
  "swagger": "2.0",
  "info": {
    "description": "A RESTful API that serves a catalog of Product data.",
    "title": "Catalog API",
    "version": "11.0.1"
  },
  "host": "127.0.0.1:%1$s",
  "basePath": "/",
  "schemes": [
    "http"
  ],
  "consumes": [
    "application/json",
    "application/vnd.pgrst.object+json",
    "text/csv"
  ],
  "produces": [
    "application/json",
    "application/vnd.pgrst.object+json",
    "text/csv"
  ],
  "paths": {
    "/": {
      "get": {
        "tags": [
          "Introspection"
        ],
        "summary": "OpenAPI description (this document)",
        "produces": [
          "application/openapi+json",
          "application/json"
        ],
        "responses": {
          "200": {
            "description": "OK"
          }
        }
      }
    },
    "/product": {
      "get": {
        "tags": [
          "product"
        ],
        "parameters": [
          {
            "$ref": "#/parameters/rowFilter.product.id"
          },
          {
            "$ref": "#/parameters/rowFilter.product.created_at"
          },
          {
            "$ref": "#/parameters/rowFilter.product.updated_at"
          },
          {
            "$ref": "#/parameters/rowFilter.product.name"
          },
          {
            "$ref": "#/parameters/rowFilter.product.price"
          },
          {
            "$ref": "#/parameters/select"
          },
          {
            "$ref": "#/parameters/order"
          },
          {
            "$ref": "#/parameters/range"
          },
          {
            "$ref": "#/parameters/rangeUnit"
          },
          {
            "$ref": "#/parameters/offset"
          },
          {
            "$ref": "#/parameters/limit"
          },
          {
            "$ref": "#/parameters/preferCount"
          }
        ],
        "responses": {
          "200": {
            "description": "OK",
            "schema": {
              "items": {
                "$ref": "#/definitions/product"
              },
              "type": "array"
            }
          },
          "206": {
            "description": "Partial Content"
          }
        }
      },
      "post": {
        "tags": [
          "product"
        ],
        "parameters": [
          {
            "$ref": "#/parameters/body.product"
          },
          {
            "$ref": "#/parameters/select"
          },
          {
            "$ref": "#/parameters/preferPost"
          }
        ],
        "responses": {
          "201": {
            "description": "Created"
          }
        }
      },
      "delete": {
        "tags": [
          "product"
        ],
        "parameters": [
          {
            "$ref": "#/parameters/rowFilter.product.id"
          },
          {
            "$ref": "#/parameters/rowFilter.product.created_at"
          },
          {
            "$ref": "#/parameters/rowFilter.product.updated_at"
          },
          {
            "$ref": "#/parameters/rowFilter.product.name"
          },
          {
            "$ref": "#/parameters/rowFilter.product.price"
          },
          {
            "$ref": "#/parameters/preferReturn"
          }
        ],
        "responses": {
          "204": {
            "description": "No Content"
          }
        }
      },
      "patch": {
        "tags": [
          "product"
        ],
        "parameters": [
          {
            "$ref": "#/parameters/rowFilter.product.id"
          },
          {
            "$ref": "#/parameters/rowFilter.product.created_at"
          },
          {
            "$ref": "#/parameters/rowFilter.product.updated_at"
          },
          {
            "$ref": "#/parameters/rowFilter.product.name"
          },
          {
            "$ref": "#/parameters/rowFilter.product.price"
          },
          {
            "$ref": "#/parameters/body.product"
          },
          {
            "$ref": "#/parameters/preferReturn"
          }
        ],
        "responses": {
          "204": {
            "description": "No Content"
          }
        }
      }
    }
  },
  "definitions": {
    "product": {
      "properties": {
        "id": {
          "description": "Note:\nThis is a Primary Key.<pk/>",
          "format": "uuid",
          "type": "string"
        },
        "created_at": {
          "format": "timestamp with time zone",
          "type": "string"
        },
        "updated_at": {
          "format": "timestamp with time zone",
          "type": "string"
        },
        "name": {
          "format": "text",
          "type": "string"
        },
        "price": {
          "format": "integer",
          "type": "integer"
        }
      },
      "type": "object"
    }
  },
  "parameters": {
    "preferParams": {
      "name": "Prefer",
      "description": "Preference",
      "required": false,
      "enum": [
        "params=single-object"
      ],
      "in": "header",
      "type": "string"
    },
    "preferReturn": {
      "name": "Prefer",
      "description": "Preference",
      "required": false,
      "enum": [
        "return=representation",
        "return=minimal",
        "return=none"
      ],
      "in": "header",
      "type": "string"
    },
    "preferCount": {
      "name": "Prefer",
      "description": "Preference",
      "required": false,
      "enum": [
        "count=none"
      ],
      "in": "header",
      "type": "string"
    },
    "preferPost": {
      "name": "Prefer",
      "description": "Preference",
      "required": false,
      "enum": [
        "return=representation",
        "return=minimal",
        "return=none",
        "resolution=ignore-duplicates",
        "resolution=merge-duplicates"
      ],
      "in": "header",
      "type": "string"
    },
    "select": {
      "name": "select",
      "description": "Filtering Columns",
      "required": false,
      "in": "query",
      "type": "string"
    },
    "on_conflict": {
      "name": "on_conflict",
      "description": "On Conflict",
      "required": false,
      "in": "query",
      "type": "string"
    },
    "order": {
      "name": "order",
      "description": "Ordering",
      "required": false,
      "in": "query",
      "type": "string"
    },
    "range": {
      "name": "Range",
      "description": "Limiting and Pagination",
      "required": false,
      "in": "header",
      "type": "string"
    },
    "rangeUnit": {
      "name": "Range-Unit",
      "description": "Limiting and Pagination",
      "required": false,
      "default": "items",
      "in": "header",
      "type": "string"
    },
    "offset": {
      "name": "offset",
      "description": "Limiting and Pagination",
      "required": false,
      "in": "query",
      "type": "string"
    },
    "limit": {
      "name": "limit",
      "description": "Limiting and Pagination",
      "required": false,
      "in": "query",
      "type": "string"
    },
    "body.product": {
      "name": "product",
      "description": "product",
      "required": false,
      "in": "body",
      "schema": {
        "$ref": "#/definitions/product"
      }
    },
    "rowFilter.product.id": {
      "name": "id",
      "required": false,
      "format": "uuid",
      "in": "query",
      "type": "string"
    },
    "rowFilter.product.created_at": {
      "name": "created_at",
      "required": false,
      "format": "timestamp with time zone",
      "in": "query",
      "type": "string"
    },
    "rowFilter.product.updated_at": {
      "name": "updated_at",
      "required": false,
      "format": "timestamp with time zone",
      "in": "query",
      "type": "string"
    },
    "rowFilter.product.name": {
      "name": "name",
      "required": false,
      "format": "text",
      "in": "query",
      "type": "string"
    },
    "rowFilter.product.price": {
      "name": "price",
      "required": false,
      "format": "integer",
      "in": "query",
      "type": "string"
    }
  },
  "externalDocs": {
  }
}
  $json$,
  current_setting('custom.swagger_port'));
begin
  return openapi;
end
$function$ language plpgsql;

grant execute on function api.root to web_anon;

comment on schema api is
$$Catalog API
A RESTful API that serves a catalog of Product data.$$;
