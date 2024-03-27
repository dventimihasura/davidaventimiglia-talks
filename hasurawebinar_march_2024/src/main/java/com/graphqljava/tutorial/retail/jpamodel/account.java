package com.graphqljava.tutorial.retail.jpamodel;

import jakarta.persistence.*;
import java.util.*;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import com.google.gson.annotations.Expose;

@Entity @Table(name = "account")
public class account extends AbstractModel {
    @Id @GeneratedValue @Expose
    public UUID id;

    @Column(name = "created_at") @CreationTimestamp @Expose
    public Date created_at;

    @Column(name = "updated_at") @UpdateTimestamp @Expose
    public Date updated_at;

    @Expose
    public String name;

    @OneToMany(fetch = FetchType.LAZY) @JoinColumn(name = "account_id") @Expose
    public Set<order> orders = new HashSet<>();
}
