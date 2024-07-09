package com.graphqljava.tutorial.retail.models;

import java.util.UUID;

public class RetailModels {
    public static
	record account
	(UUID id,
	 String name,
	 String created_at,
	 String updated_at) {}

    public static
	record order_detail
	(UUID id,
	 UUID order_id,
	 UUID product_id,
	 Integer units,
	 String created_at,
	 String updated_at) {}

    public static
	record order
	(UUID id,
	 UUID account_id,
	 String status,
	 String created_at,
	 String updated_at) {}

    public static
	record product
	(UUID id,
	 String name,
	 Integer price,
	 String created_at,
	 String updated_at) {}}
