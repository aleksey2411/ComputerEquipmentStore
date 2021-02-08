package com.yakubovskiy.project.entity;

import com.yakubovskiy.project.enums.OrderStatus;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@Table(name = "orders")
@Entity
public class Order {
    @Id
    @GeneratedValue
    @Type(type = "pg-uuid")
    @Column(name = "order_id")
    private UUID id;

    @Column(name = "order_creation_date")
    private Long creationDate;
    @Column(name = "order_closing_date")
    private Long closingDate;
    @Column(name = "order_status")
    @Enumerated(EnumType.STRING)
    private OrderStatus orderStatus;
    @OneToMany(mappedBy = "order", fetch = FetchType.EAGER)
    @Fetch(value = FetchMode.SUBSELECT)
    private List<OrderItem> orderItems;
    @ManyToOne
    @JoinColumn(name = "user_id_fk")
    private User user;
    @Column(name = "order_grand_total")
    private Double grandTotal;

    @Builder
    public Order(UUID id, Long creationDate, Long closingDate, OrderStatus orderStatus, List<OrderItem> orderItems, Double grandTotal, User user) {
        this.id = id;
        this.creationDate = creationDate;
        this.closingDate = closingDate;
        this.orderStatus = orderStatus;
        this.orderItems = orderItems;
        this.user = user;
        this.grandTotal = grandTotal;
    }
}