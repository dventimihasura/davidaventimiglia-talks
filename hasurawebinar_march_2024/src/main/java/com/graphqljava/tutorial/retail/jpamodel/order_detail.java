package com.graphqljava.tutorial.retail.jpamodel;

import jakarta.persistence.*;
import java.util.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import com.google.gson.annotations.Expose;

@Entity @Table(name = "order_detail")
public class order_detail extends AbstractModel {
    @Id @GeneratedValue @Expose
    public UUID id;

    @Column(name = "created_at") @CreationTimestamp @Expose
    public Date created_at;

    @Column(name = "updated_at") @UpdateTimestamp @Expose
    public Date updated_at;

    @Expose
    public int units;

    @ManyToOne(fetch = FetchType.LAZY) @Expose
    public order_detail order;

    @ManyToOne(fetch = FetchType.LAZY) @Expose
    public product product;
}
