package com.graphqljava.tutorial.retail.jpamodel;

import jakarta.persistence.*;
import java.util.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import com.google.gson.annotations.Expose;

@Entity @Table(name = "order_detail")
public class OrderDetail extends AbstractModel {
    @Id @GeneratedValue @Expose
    public UUID id;

    @Column(name = "created_at") @CreationTimestamp @Expose
    public Date createdAt;

    @Column(name = "updated_at") @UpdateTimestamp @Expose
    public Date updatedAt;

    @Expose
    public int units;

    @ManyToOne(fetch=FetchType.LAZY) @Expose
    public Product product;
}
