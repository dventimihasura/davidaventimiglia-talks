package com.graphqljava.tutorial.retail.controllers;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import org.hibernate.Session;
import org.hibernate.jpa.SpecHints;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.graphql.data.ArgumentValue;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.graphql.data.method.annotation.SchemaMapping;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.jdbc.core.simple.JdbcClient.StatementSpec;
import org.springframework.stereotype.Controller;

import com.graphqljava.tutorial.retail.jpamodel.account;
import com.graphqljava.tutorial.retail.jpamodel.order;
import com.graphqljava.tutorial.retail.jpamodel.order_detail;
import com.graphqljava.tutorial.retail.jpamodel.product;

import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

public class Controllers {
    @Controller public static class accountController {
	@Autowired JdbcClient jdbcClient;
	private EntityManagerFactory emFactory = Persistence.createEntityManagerFactory("example");
	List<account> account_by_pks (UUID... ids) {
	    var session = emFactory.createEntityManager().unwrap(Session.class);
	    var accounts = session.createEntityGraph(account.class);
	    var orders = accounts.addSubgraph("orders");
	    var orderdetails = orders.addSubgraph("order_details");
	    var products = orderdetails.addSubgraph("product");
	    var hints = new HashMap<>();
	    hints.put(SpecHints.HINT_SPEC_FETCH_GRAPH, accounts);
	    return session.byMultipleIds(account.class)
		.withFetchGraph(accounts)
		.multiLoad(ids);}
	@SchemaMapping account
	    account (order order) {
	    return account_by_pk(order.id.toString());}
	@QueryMapping List<account>
	    account (ArgumentValue<Integer> limit) {
	    StatementSpec
		spec = limit.isOmitted() ?
		jdbcClient.sql("select id from account") :
		jdbcClient.sql("select id from account limit ?").param(limit.value());
	    return account_by_pks(spec.query(new ResultSetExtractor<List<UUID>>() {
		    public List<UUID> extractData (ResultSet rs) throws SQLException, DataAccessException {
			List<UUID> ids = new ArrayList<>();
			while (rs.next()) ids.add(UUID.fromString(rs.getString(1)));
			return ids;}}).toArray(new UUID[1]));}
	@QueryMapping account account_by_pk (@Argument String id) {
	    for (account a : account_by_pks(UUID.fromString(id))) return a;
	    return null;}}

    @Controller public static class orderController {
	@Autowired JdbcClient jdbcClient;
	private EntityManagerFactory emFactory = Persistence.createEntityManagerFactory("example");
	List<order> order_by_pks (UUID... ids) {
	    var session = emFactory.createEntityManager().unwrap(Session.class);
	    var orders = session.createEntityGraph(order.class);
	    var orderdetails = orders.addSubgraph("order_detail");
	    var products = orderdetails.addSubgraph("products");
	    var hints = new HashMap<>();
	    hints.put(SpecHints.HINT_SPEC_FETCH_GRAPH, orders);
	    return session.byMultipleIds(order.class)
		.withFetchGraph(orders)
		.multiLoad(ids);}
	@SchemaMapping order
	    order (order_detail orderdetail) {
	    return order_by_pk(orderdetail.order.id.toString());}
	@QueryMapping List<order>
	    order (ArgumentValue<Integer> limit) {
	    StatementSpec
		spec = limit.isOmitted() ?
		jdbcClient.sql("select id from order") :
		jdbcClient.sql("select id from order limit ?").param(limit.value());
	    return order_by_pks(spec.query(new ResultSetExtractor<List<UUID>>() {
		    public List<UUID> extractData (ResultSet rs) throws SQLException, DataAccessException {
			List<UUID> ids = new ArrayList<>();
			while (rs.next()) ids.add(UUID.fromString(rs.getString(1)));
			return ids;}}).toArray(new UUID[1]));}
	@QueryMapping order order_by_pk (@Argument String id) {
	    for (order a : order_by_pks(UUID.fromString(id))) return a;
	    return null;}}

    @Controller public static class order_detailController {
	@Autowired JdbcClient jdbcClient;
	private EntityManagerFactory emFactory = Persistence.createEntityManagerFactory("example");
	List<order_detail> order_detail_by_pks (UUID... ids) {
	    var session = emFactory.createEntityManager().unwrap(Session.class);
	    var orderdetails = session.createEntityGraph(order_detail.class);
	    var products = orderdetails.addSubgraph("products");
	    var hints = new HashMap<>();
	    hints.put(SpecHints.HINT_SPEC_FETCH_GRAPH, orderdetails);
	    return session.byMultipleIds(order_detail.class)
		.withFetchGraph(orderdetails)
		.multiLoad(ids);}
	// @QueryMapping List<order_detail>
	//     order_detail (product product) {
	//     StatementSpec
	// 	spec = jdbcClient.sql("select id from order_detail where product_id = ?").param(product.id.toString());
	//     return order_detail_by_pks(spec.query(new ResultSetExtractor<List<UUID>>() {
	// 	    public List<UUID> extractData (ResultSet rs) throws SQLException, DataAccessException {
	// 		List<UUID> ids = new ArrayList<>();
	// 		while (rs.next()) ids.add(UUID.fromString(rs.getString(1)));
	// 		return ids;}}).toArray(new UUID[1]));}
	@QueryMapping List<order_detail>
	    order_detail (ArgumentValue<Integer> limit) {
	    StatementSpec
		spec = limit.isOmitted() ?
		jdbcClient.sql("select id from order_detail") :
		jdbcClient.sql("select id from order_detail limit ?").param(limit.value());
	    return order_detail_by_pks(spec.query(new ResultSetExtractor<List<UUID>>() {
		    public List<UUID> extractData (ResultSet rs) throws SQLException, DataAccessException {
			List<UUID> ids = new ArrayList<>();
			while (rs.next()) ids.add(UUID.fromString(rs.getString(1)));
			return ids;}}).toArray(new UUID[1]));}
	@QueryMapping order_detail order_detail_by_pk (@Argument String id) {
	    for (order_detail a : order_detail_by_pks(UUID.fromString(id))) return a;
	    return null;}}

    @Controller public static class productController {
	@Autowired JdbcClient jdbcClient;
	private EntityManagerFactory emFactory = Persistence.createEntityManagerFactory("example");
	List<product> product_by_pks (UUID... ids) {
	    var session = emFactory.createEntityManager().unwrap(Session.class);
	    var products = session.createEntityGraph(product.class);
	    var hints = new HashMap<>();
	    hints.put(SpecHints.HINT_SPEC_FETCH_GRAPH, products);
	    return session.byMultipleIds(product.class)
		.withFetchGraph(products)
		.multiLoad(ids);}
	@QueryMapping List<product>
	    product (ArgumentValue<Integer> limit) {
	    StatementSpec
		spec = limit.isOmitted() ?
		jdbcClient.sql("select id from product") :
		jdbcClient.sql("select id from product limit ?").param(limit.value());
	    return product_by_pks(spec.query(new ResultSetExtractor<List<UUID>>() {
		    public List<UUID> extractData (ResultSet rs) throws SQLException, DataAccessException {
			List<UUID> ids = new ArrayList<>();
			while (rs.next()) ids.add(UUID.fromString(rs.getString(1)));
			return ids;}}).toArray(new UUID[1]));}
	@QueryMapping product product_by_pk (@Argument String id) {
	    for (product a : product_by_pks(UUID.fromString(id))) return a;
	    return null;}}}

