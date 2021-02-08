package com.yakubovskiy.project.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Value;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;
import java.util.UUID;

@Builder
@Value
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CategoryDto implements Serializable {
    @ApiModelProperty(position = 1)
    UUID id;
    @ApiModelProperty(position = 2)
    @NotBlank(message = "Enter category title")
    String title;
}
