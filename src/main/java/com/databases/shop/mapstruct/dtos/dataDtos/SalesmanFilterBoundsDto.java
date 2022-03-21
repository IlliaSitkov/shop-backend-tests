package com.databases.shop.mapstruct.dtos.dataDtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Setter;

import javax.validation.constraints.Min;

@Setter
public class SalesmanFilterBoundsDto {

    @JsonProperty("max_order")
    @Min(0)
    private int maxOrderCount;

    @JsonProperty("max_income")
    @Min(0)
    private double maxIncome;

}
