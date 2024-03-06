package com.graphqljava.tutorial.retail.controllers;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.graphql.data.ArgumentValue;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.graphql.data.method.annotation.SchemaMapping;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.jdbc.core.simple.JdbcClient.StatementSpec;
import org.springframework.stereotype.Controller;

import com.graphqljava.tutorial.retail.models.Schema.order_detail;
import com.graphqljava.tutorial.retail.models.Schema.product;

@Controller class ProductController {

    @Autowired JdbcClient jdbcClient;

    RowMapper<product>
	productMapper = new RowMapper<product>() {
		public product mapRow (ResultSet rs, int rowNum) throws SQLException {
		    return new product
		    (UUID.fromString(rs.getString("id")),
		     rs.getString("name"),
		     rs.getInt("price"),
		     rs.getString("created_at"),
		     rs.getString("updated_at"));}};

    @SchemaMapping product
	product (order_detail order_detail) {
	return
	    jdbcClient
	    .sql("select * from product where id = ? limit 1")
	    .param(order_detail.product_id())
	    .query(productMapper)
	    .optional()
	    .get();}

    @QueryMapping List<product>
	products (ArgumentValue<Integer> limit) {
	StatementSpec
	    spec = limit.isOmitted() ?
	    jdbcClient.sql("select * from product") :
	    jdbcClient.sql("select * from product limit ?").param(limit.value());
	return
	    spec
	    .query(productMapper)
	    .list();}

    @QueryMapping product
	product_by_pk (@Argument String id) {
	return
	    jdbcClient
	    .sql("select * from product where id = ? limit 1")
	    .param(UUID.fromString(id))
	    .query(productMapper)
	    .optional()
	    .get();}}
