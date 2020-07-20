package com.spring.reactiveprogramming.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class Book {
    private String title;
    private BigDecimal price;
    private String category;

}
