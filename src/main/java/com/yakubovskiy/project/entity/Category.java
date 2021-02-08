package com.yakubovskiy.project.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.util.Set;
import java.util.UUID;

@Data
@NoArgsConstructor
@Table(name = "categories")
@Entity
public class Category {
    @Id
    @GeneratedValue
    @Type(type = "pg-uuid")
    @Column(name = "category_id")
    private UUID id;

    @Column(name = "category_title")
    private String title;

    @ToString.Exclude
    @JsonIgnore
    @OneToMany(mappedBy = "category")
    private Set<Product> products;

    @Builder
    public Category(UUID id, String title, Set<Product> products) {
        this.id = id;
        this.title = title;
        this.products = products;
    }
}
