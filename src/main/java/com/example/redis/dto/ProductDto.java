package com.example.redis.dto;

import lombok.*;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;
@SuperBuilder
@Getter
@Setter
@NoArgsConstructor
public class ProductDto {
    private String name;
    private String description;
    private BigDecimal price;
    private Integer quantity;

    @SuperBuilder
    @Getter
    @Setter
    @NoArgsConstructor
    public static class Update extends ProductDto{

    }

    @SuperBuilder
    @Getter
    @Setter
    @NoArgsConstructor
    public static class Create extends ProductDto{

    }
}
