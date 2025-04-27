package com.discounts.discounts.model.discount;

import com.discounts.discounts.model.BrandTier;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public abstract class Discount {
    private String id;
    private String name;
    private String description;
    private BigDecimal discountPercentage;
    private BigDecimal discountAmount;
    private boolean isActive;
    private String[] restrictedCategories;
    private String[] restrictedBrands;
    private String[] applicableCustomerTiers;
    
    public abstract boolean isApplicable(String category, String brand, BrandTier brandTier, String customerTier);
} 