package com.yakubovskiy.project.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Value;

import java.io.Serializable;
import java.util.UUID;

@Builder
@Value
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CartItemDto implements Serializable {
    UUID id;
    String productTitle;
    Integer quantity;
    Double totalPrice;
}
