package com.yakubovskiy.project.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.yakubovskiy.project.entity.User;
import lombok.Builder;
import lombok.Value;

import java.io.Serializable;
import java.util.List;
import java.util.UUID;

@Builder
@Value
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CartDto implements Serializable {
    UUID id;
    User user;
    Double grandTotal;
    List<CartItemDto> cartItems;
}
