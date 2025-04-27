package com.discounts.discounts.model;

import lombok.Builder;
import lombok.Data;
import java.math.BigDecimal;

@Data
@Builder
public class Product {
   private String id;
   private String brand;
   private BrandTier brandTier;
   private String category;
   private BigDecimal basePrice;
   private BigDecimal currentPrice; // After brand/category discount
} 