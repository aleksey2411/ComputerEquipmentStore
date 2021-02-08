package com.yakubovskiy.project.entity;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.io.Serializable;
import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@Table(name = "cart")
@Entity
public class Cart implements Serializable {
    @Id
    @GeneratedValue
    @Type(type = "pg-uuid")
    @Column(name = "cart_id")
    private UUID id;

    @OneToMany(mappedBy = "cart", fetch = FetchType.EAGER)
    @Fetch(value = FetchMode.SUBSELECT)
    private List<CartItem> cartItems;

    @OneToOne
    @JoinColumn(name = "user_id_fk")
    private User user;

    @Column(name = "cart_grand_total")
    private Double grandTotal;

    @Builder
    public Cart(UUID id, List<CartItem> cartItems, User user, Double grandTotal) {
        this.id = id;
        this.cartItems = cartItems;
        this.user = user;
        this.grandTotal = grandTotal;
    }

}
