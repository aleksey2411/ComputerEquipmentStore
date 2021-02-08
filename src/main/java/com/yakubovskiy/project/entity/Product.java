package com.yakubovskiy.project.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.util.Set;
import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "products")
@Entity
public class Product {
    @Id
    @GeneratedValue
    @Type(type = "pg-uuid")
    @Column(name = "product_id")
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "category_id_fk")
    private Category category;

    @ToString.Exclude
    @JsonIgnore
    @OneToMany(mappedBy = "product")
    private Set<Review> reviews;

    @ToString.Exclude
    @JsonIgnore
    @OneToMany(mappedBy = "product")
    private Set<CartItem> cartItems;

    @OneToMany(mappedBy = "product")
    @ToString.Exclude
    @JsonIgnore
    private Set<OrderItem> orderItems;

    @Column(name = "product_title")
    private String title;

    @Column(name = "product_price")
    private double price;

    @Column(name = "product_model")
    private String model;

    @JsonIgnore
    @Column(name = "product_quantity")
    private Integer quantity;
}
