package com.yakubovskiy.project.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.yakubovskiy.project.entity.User;
import com.yakubovskiy.project.enums.OrderStatus;
import lombok.Builder;
import lombok.Value;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Builder
@Value
@JsonInclude(JsonInclude.Include.NON_NULL)
public class OrderDto implements Serializable {
    UUID id;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm a z")
    Date creationDate;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm a z")
    Date closingDate;
    OrderStatus orderStatus;
    List<OrderItemDto> orderItems;
    Double grandTotal;
    User user;
}
