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
public class BrandDiscount extends Discount {
    private String brandName;
    private BrandTier applicableBrandTier;

    @Override
    public boolean isApplicable(String category, String brand, BrandTier brandTier, String customerTier) {
        if (!isActive()) {
            return false;
        }
        
        // Check if the discount applies to this brand
        if (brandName != null && !brandName.equalsIgnoreCase(brand)) {
            return false;
        }
        
        // Check if the discount applies to this brand tier
        if (applicableBrandTier != null && brandTier != applicableBrandTier) {
            return false;
        }
        
        return true;
    }
} 