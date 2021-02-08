package com.yakubovskiy.project.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.yakubovskiy.project.entity.Product;
import com.yakubovskiy.project.entity.User;
import lombok.Builder;
import lombok.Value;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Builder
@Value
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ReviewDto implements Serializable {
    UUID id;
    UUID parentId;
    @NotBlank(message = "Enter your review")
    String title;
    Product product;
    String username;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm a z")
    Date creationDate;
    List<ReviewDto> answers;
}
