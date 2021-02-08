package com.yakubovskiy.project.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "product_review")
@Entity
public class Review {
    @Id
    @GeneratedValue
    @Type(type = "pg-uuid")
    @Column(name = "product_review_id")
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "product_id_fk")
    private Product product;

    @ManyToOne
    @JoinColumn(name = "user_id_fk")
    private User user;

    @Column(name = "parent_id")
    private UUID parentId;

    @Column(name = "title")
    private String title;

    @Column(name = "creation_date")
    private Long creationDate;

}
