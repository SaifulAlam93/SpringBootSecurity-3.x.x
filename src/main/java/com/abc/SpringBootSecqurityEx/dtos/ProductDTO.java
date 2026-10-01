package com.abc.SpringBootSecqurityEx.dtos;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

@Getter
@Setter
@NoArgsConstructor
public class ProductDTO {
    private Long id;

    @NotBlank
    @Size(max = 150)
    private String name;

    @NotBlank
    @Size(max = 100)
    private String category;

    @NotNull
    @DecimalMin("0.01")
    private BigDecimal price;

    @Size(max = 2000)
    private String description;

    @NotNull
    @Min(0)
    private Integer stock;

    private Boolean active;
    private Boolean premium;

    @Size(max = 1000)
    private String imageUrl;

    private OffsetDateTime createdAt;
}
