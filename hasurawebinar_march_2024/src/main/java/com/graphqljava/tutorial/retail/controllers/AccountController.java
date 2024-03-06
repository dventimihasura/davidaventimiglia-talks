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

@Controller class AccountController {

    @Autowired JdbcClient jdbcClient;

    RowMapper<account>
	accountMapper = new RowMapper<>() {
	    public account mapRow (ResultSet rs, int rowNum) throws SQLException {
		return
		    new account
		    (UUID.fromString(rs.getString("ID")),
		     rs.getString("name"),
		     rs.getString("created_at"),
		     rs.getString("updated_at"));}};

    @SchemaMapping account
	account (order order) {
	return
	    jdbcClient
	    .sql("select * from account where id = ? limit 1")
	    .param(order.account_id())
	    .query(accountMapper)
	    .optional()
	    .get();}

    @QueryMapping List<account>
	accounts (ArgumentValue<Integer> limit) {
	StatementSpec
	    spec = limit.isOmitted() ?
	    jdbcClient.sql("select * from account") :
	    jdbcClient.sql("select * from account limit ?").param(limit.value());
	return
	    spec
	    .query(accountMapper)
	    .list();}

    @QueryMapping account
	account_by_pk (@Argument String id) {
	return
	    jdbcClient
	    .sql("select * from account where id = ? limit 1")
	    .param(UUID.fromString(id))
	    .query(accountMapper)
	    .optional()
	    .get();}}

