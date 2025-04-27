package com.discounts.discounts.model.discount;

import com.discounts.discounts.model.BrandTier;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@EqualsAndHashCode(callSuper = true)
@SuperBuilder
@NoArgsConstructor
public class CategoryDiscount extends Discount {
    private String categoryName;

    @Override
    public boolean isApplicable(String category, String brand, BrandTier brandTier, String customerTier) {
        if (!isActive()) {
            return false;
        }
        
        // Check if the discount applies to this category
        if (categoryName != null && !categoryName.equalsIgnoreCase(category)) {
            return false;
        }
        
        return true;
    }
} 