package com.yakubovskiy.project.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.util.UUID;

@Data
@NoArgsConstructor
@Table(name = "order_item")
@Entity
public class OrderItem {
    @Id
    @GeneratedValue
    @Type(type = "pg-uuid")
    @Column(name = "order_item_id")
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "product_id_fk")
    private Product product;

    @ManyToOne
    @ToString.Exclude
    @JsonIgnore
    @JoinColumn(name = "order_id_fk")
    private Order order;

    @Column(name = "order_item_quantity")
    private Integer quantity;

    @Column(name = "order_item_total_price")
    private Double totalPrice;

    @Builder
    public OrderItem(UUID id, Product product, Order order, Double totalPrice, Integer quantity) {
        this.id = id;
        this.product = product;
        this.order = order;
        this.totalPrice = totalPrice;
        this.quantity = quantity;
    }
}