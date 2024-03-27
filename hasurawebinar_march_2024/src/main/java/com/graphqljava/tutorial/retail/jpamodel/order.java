package com.graphqljava.tutorial.retail.jpamodel;

import jakarta.persistence.*;
import java.util.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import com.google.gson.annotations.Expose;

@Entity @Table(name = "`order`")
public class order extends AbstractModel {
    @Id @GeneratedValue @Expose
    public UUID id;

    @Column(name = "created_at") @CreationTimestamp @Expose
    public Date created_at;

    @Column(name = "updated_at") @UpdateTimestamp @Expose
    public Date updated_at;

    @Expose
    public UUID account_id;

    @Expose
    public String status;

    @OneToMany(fetch = FetchType.LAZY) @JoinColumn(name = "order_id") @Expose
    public Set<order_detail> order_details = new HashSet<>();
}
