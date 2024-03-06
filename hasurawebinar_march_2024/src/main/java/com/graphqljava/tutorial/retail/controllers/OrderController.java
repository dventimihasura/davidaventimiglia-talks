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

import com.graphqljava.tutorial.retail.models.Schema.account;
import com.graphqljava.tutorial.retail.models.Schema.order;
import com.graphqljava.tutorial.retail.models.Schema.order_detail;

@Controller class OrderController {

    @Autowired JdbcClient jdbcClient;

    RowMapper<order>
	orderMapper = new RowMapper<order>() {
		public order mapRow (ResultSet rs, int rowNum) throws SQLException {
		    return new order
		    (UUID.fromString(rs.getString("id")),
		     UUID.fromString(rs.getString("account_id")),
		     rs.getString("status"),
		     rs.getString("created_at"),
		     rs.getString("updated_at"));}};

    @SchemaMapping order
	order (order_detail order_detail) {
	return
	    jdbcClient
	    .sql("select * from \"order\" where id = ? limit 1")
	    .param(order_detail.order_id())
	    .query(orderMapper)
	    .optional()
	    .get();}

    @SchemaMapping List<order>
	orders (account account, ArgumentValue<Integer> limit) {
	StatementSpec
	    spec = limit.isOmitted() ?
	    jdbcClient.sql("select * from \"order\" where account_id = ?").param(account.id()) :
	    jdbcClient.sql("select * from \"order\" where account_id = ? limit ?").param(account.id()).param(limit.value());
	return
	    spec
	    .query(orderMapper)
	    .list();}

    @QueryMapping List<order>
	orders (ArgumentValue<Integer> limit) {
	StatementSpec
	    spec = limit.isOmitted() ?
	    jdbcClient.sql("select * from \"order\"") :
	    jdbcClient.sql("select * from \"order\" limit ?").param(limit.value());
	return
	    spec
	    .query(orderMapper)
	    .list();}

    @QueryMapping order
	order_by_pk (@Argument String id) {
	return
	    jdbcClient
	    .sql("select * from \"order\" where id = ? limit 1")
	    .param(UUID.fromString(id))
	    .query(orderMapper)
	    .optional()
	    .get();}}
