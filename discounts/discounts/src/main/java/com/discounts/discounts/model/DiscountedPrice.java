package com.discounts.discounts.model;

import lombok.Builder;
import lombok.Data;
import java.math.BigDecimal;
import java.util.Map;

@Data
@Builder
public class DiscountedPrice {
   private BigDecimal originalPrice;
   private BigDecimal finalPrice;
   private Map<String, BigDecimal> appliedDiscounts; // discount_name -> amount
   private String message;
} 