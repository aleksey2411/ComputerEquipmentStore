package com.yakubovskiy.project.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.util.UUID;


@Data
@NoArgsConstructor
@Table(name = "cart_item")
@Entity
public class CartItem {
    @Id
    @GeneratedValue
    @Type(type = "pg-uuid")
    @Column(name = "cart_item_id")
    private UUID id;

    @ManyToOne
    @ToString.Exclude
    @JsonIgnore
    @JoinColumn(name = "cart_id_fk")
    private Cart cart;

    @ManyToOne
    @JoinColumn(name = "product_id_fk")
    private Product product;

    @Column(name = "cart_item_quantity")
    private Integer quantity;

    @Column(name = "cart_item_total_price")
    private Double totalPrice;

    @Builder
    public CartItem(UUID id, Cart cart, Product product, Integer quantity, Double totalPrice) {
        this.id = id;
        this.cart = cart;
        this.product = product;
        this.quantity = quantity;
        this.totalPrice = totalPrice;
    }
}
