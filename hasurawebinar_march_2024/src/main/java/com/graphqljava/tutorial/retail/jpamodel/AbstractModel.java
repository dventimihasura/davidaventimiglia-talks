package com.graphqljava.tutorial.retail.jpamodel;

import com.google.gson.*;

public abstract class AbstractModel {
    public static Gson gson = new GsonBuilder()
	.serializeNulls()
	.setFieldNamingPolicy(FieldNamingPolicy.UPPER_CAMEL_CASE)
	.setPrettyPrinting()
	.setVersion(1.0)
	.excludeFieldsWithoutExposeAnnotation()
	.create();
    
    public String toString() {
	return gson.toJson(this);
    }
}
