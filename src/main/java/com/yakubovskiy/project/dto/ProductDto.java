package com.yakubovskiy.project.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.yakubovskiy.project.entity.Category;
import io.swagger.annotations.ApiModelProperty;
import io.swagger.annotations.ApiParam;
import lombok.Builder;
import lombok.Value;

import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotBlank;
import java.io.Serializable;
import java.util.UUID;

@Builder
@Value
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ProductDto implements Serializable {
    @ApiModelProperty(position = 1,hidden = true)
    UUID id;
    @ApiModelProperty(position = 2)
    @NotBlank(message = "Enter product title")
    String title;
    @ApiModelProperty(position = 3)
    @NotBlank(message = "Enter product model")
    String model;
    @ApiModelProperty(position = 4)
    @DecimalMin(value = "0.01", message = "Price should be at least 0.01$")
    Double price;
    @ApiModelProperty(position = 5)
    @DecimalMin(value = "1", message = "Quantity should be at least 1")
    Integer quantity;
    @ApiModelProperty(position = 6)
    CategoryDto category;
}
