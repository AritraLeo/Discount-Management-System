package com.discounts.discounts.model.discount;

import com.discounts.discounts.model.BrandTier;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.Arrays;

@Data
@EqualsAndHashCode(callSuper = true)
@SuperBuilder
@NoArgsConstructor
public class VoucherDiscount extends Discount {
    private String code;
    private boolean isOneTimeUse;
    
    @Override
    public boolean isApplicable(String category, String brand, BrandTier brandTier, String customerTier) {
        if (!isActive()) {
            return false;
        }
        
        // Check if this category is restricted
        if (getRestrictedCategories() != null && Arrays.asList(getRestrictedCategories()).contains(category)) {
            return false;
        }
        
        // Check if this brand is restricted
        if (getRestrictedBrands() != null && Arrays.asList(getRestrictedBrands()).contains(brand)) {
            return false;
        }
        
        // Check if customer tier is applicable
        if (getApplicableCustomerTiers() != null && getApplicableCustomerTiers().length > 0) {
            if (!Arrays.asList(getApplicableCustomerTiers()).contains(customerTier)) {
                return false;
            }
        }
        
        return true;
    }
} 