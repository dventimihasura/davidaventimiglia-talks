package com.graphqljava.tutorial.retail.controllers;

import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import org.hibernate.Session;
import org.hibernate.jpa.SpecHints;
import org.springframework.graphql.data.ArgumentValue;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

import com.graphqljava.tutorial.retail.jpamodel.account;
import com.graphqljava.tutorial.retail.jpamodel.order;
import com.graphqljava.tutorial.retail.jpamodel.order_detail;
import com.graphqljava.tutorial.retail.jpamodel.product;

import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import jakarta.persistence.Query;

public class Controllers {
    @Controller public static class accountController {
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
	@SuppressWarnings("unchecked") @QueryMapping List<account>
	    account (ArgumentValue<Integer> limit) {
	    Query j = emFactory.createEntityManager().createNativeQuery(limit.isOmitted() ?
									"select id from account" :
									"select id from account limit :limit", UUID.class);
	    if (!limit.isOmitted()) j.setParameter("limit", limit.value());
	    return account_by_pks((UUID[])j.getResultList().toArray(new UUID[0]));}
	@QueryMapping account account_by_pk (@Argument String id) {
	    for (account a : account_by_pks(UUID.fromString(id))) return a;
	    return null;}}

    @Controller public static class orderController {
	private EntityManagerFactory emFactory = Persistence.createEntityManagerFactory("example");
	List<order> order_by_pks (UUID... ids) {
	    var session = emFactory.createEntityManager().unwrap(Session.class);
	    var orders = session.createEntityGraph(order.class);
	    var accounts = orders.addSubGraph("account");
	    var orderdetails = orders.addSubgraph("order_details");
	    var products = orderdetails.addSubgraph("product");
	    var hints = new HashMap<>();
	    hints.put(SpecHints.HINT_SPEC_FETCH_GRAPH, orders);
	    return session.byMultipleIds(order.class)
		.withFetchGraph(orders)
		.multiLoad(ids);}
	@SuppressWarnings("unchecked") @QueryMapping List<order>
	    order (ArgumentValue<Integer> limit) {
	    Query j = emFactory.createEntityManager().createNativeQuery(limit.isOmitted() ?
									"select id from \"order\"" :
									"select id from \"order\" limit :limit", UUID.class);
	    if (!limit.isOmitted()) j.setParameter("limit", limit.value());
	    return order_by_pks((UUID[])j.getResultList().toArray(new UUID[0]));}
	@QueryMapping order order_by_pk (@Argument String id) {
	    for (order a : order_by_pks(UUID.fromString(id))) return a;
	    return null;}}

    @Controller public static class order_detailController {
	private EntityManagerFactory emFactory = Persistence.createEntityManagerFactory("example");
	List<order_detail> order_detail_by_pks (UUID... ids) {
	    var session = emFactory.createEntityManager().unwrap(Session.class);
	    var orderdetails = session.createEntityGraph(order_detail.class);
	    var products = orderdetails.addSubgraph("product");
	    var orders = orderdetails.addSubGraph("order");
	    var hints = new HashMap<>();
	    hints.put(SpecHints.HINT_SPEC_FETCH_GRAPH, orderdetails);
	    return session.byMultipleIds(order_detail.class)
		.withFetchGraph(orderdetails)
		.multiLoad(ids);}
	@SuppressWarnings("unchecked") @QueryMapping List<order_detail>
	    order_detail (ArgumentValue<Integer> limit) {
	    Query j = emFactory.createEntityManager().createNativeQuery(limit.isOmitted() ?
									"select id from order_detail" :
									"select id from order_detail limit :limit", UUID.class);
	    if (!limit.isOmitted()) j.setParameter("limit", limit.value());
	    return order_detail_by_pks((UUID[])j.getResultList().toArray(new UUID[0]));}
	@QueryMapping order_detail order_detail_by_pk (@Argument String id) {
	    for (order_detail a : order_detail_by_pks(UUID.fromString(id))) return a;
	    return null;}}

    @Controller public static class productController {
	private EntityManagerFactory emFactory = Persistence.createEntityManagerFactory("example");
	@QueryMapping List<product> product_by_pks (UUID... ids) {
	    var session = emFactory.createEntityManager().unwrap(Session.class);
	    var products = session.createEntityGraph(product.class);
	    var hints = new HashMap<>();
	    hints.put(SpecHints.HINT_SPEC_FETCH_GRAPH, products);
	    return session.byMultipleIds(product.class)
		.withFetchGraph(products)
		.multiLoad(ids);}
	@SuppressWarnings("unchecked") @QueryMapping List<product>
	    product (ArgumentValue<Integer> limit) {
	    Query j = emFactory.createEntityManager().createNativeQuery(limit.isOmitted() ?
									"select id from product" :
									"select id from product limit :limit", UUID.class);
	    if (!limit.isOmitted()) j.setParameter("limit", limit.value());
	    return product_by_pks((UUID[])j.getResultList().toArray(new UUID[0]));}
	@QueryMapping product product_by_pk (@Argument String id) {
	    for (product a : product_by_pks(UUID.fromString(id))) return a;
	    return null;}}}

