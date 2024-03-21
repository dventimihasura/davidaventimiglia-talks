package com.graphqljava.tutorial.retail.jpamodel;

import jakarta.persistence.*;

@Entity
public class Region {
    @Id
    public String value;
    public String description;
}
