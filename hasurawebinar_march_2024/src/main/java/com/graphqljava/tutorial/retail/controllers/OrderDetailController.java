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

import com.graphqljava.tutorial.retail.models.Schema.order;
import com.graphqljava.tutorial.retail.models.Schema.order_detail;
import com.graphqljava.tutorial.retail.models.Schema.product;

@Controller class OrderDetailController {

    @Autowired JdbcClient jdbcClient;

    RowMapper<order_detail>
	orderDetailMapper = new RowMapper<order_detail>() {
		public order_detail mapRow (ResultSet rs, int rowNum) throws SQLException {
		    return new order_detail
		    (UUID.fromString(rs.getString("id")),
		     UUID.fromString(rs.getString("order_id")),
		     UUID.fromString(rs.getString("product_id")),
		     rs.getInt("units"),
		     rs.getString("created_at"),
		     rs.getString("updated_at"));}};

    @SchemaMapping List<order_detail>
	order_details (order order, ArgumentValue<Integer> limit) {
	StatementSpec
	    spec = limit.isOmitted() ?
	    jdbcClient.sql("select * from order_detail where order_id = ?").param(order.id()) :
	    jdbcClient.sql("select * from order_detail where order_id = ? limit ?").param(order.id()).param(limit.value());
	return
	    spec
	    .query(orderDetailMapper)
	    .list();}

    @SchemaMapping List<order_detail>
	order_details (product product, ArgumentValue<Integer> limit) {
	StatementSpec
	    spec = limit.isOmitted() ?
	    jdbcClient.sql("select * from order_detail where product_id = ?").param(product.id()) :
	    jdbcClient.sql("select * from order_detail where product_id = ? limit ?").param(product.id()).param(limit.value());
	return
	    spec
	    .query(orderDetailMapper)
	    .list();}

    @QueryMapping List<order_detail>
	order_details (ArgumentValue<Integer> limit) {
	StatementSpec
	    spec = limit.isOmitted() ?
	    jdbcClient.sql("select * from order_detail") :
	    jdbcClient.sql("select * from order_detail limit ?").param(limit.value());
	return
	    spec
	    .query(orderDetailMapper)
	    .list();}

    @QueryMapping order_detail
	order_detail_by_pk (@Argument String id) {
	return
	    jdbcClient
	    .sql("select * from order_detail where id = ? limit 1")
	    .param(UUID.fromString(id))
	    .query(orderDetailMapper)
	    .optional()
	    .get();}}
