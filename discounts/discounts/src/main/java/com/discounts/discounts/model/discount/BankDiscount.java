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
public class BankDiscount extends Discount {
    private String bankName;
    private String[] cardTypes; // CREDIT, DEBIT

    @Override
    public boolean isApplicable(String category, String brand, BrandTier brandTier, String customerTier) {
        // This method doesn't use these parameters as bank discounts apply based on payment info
        // It will be checked separately in the service
        return isActive();
    }
    
    public boolean isApplicableForPayment(String paymentBankName, String paymentCardType) {
        if (!isActive()) {
            return false;
        }
        
        // Check if the discount applies to this bank
        if (bankName != null && !bankName.equalsIgnoreCase(paymentBankName)) {
            return false;
        }
        
        // Check if the discount applies to this card type
        if (cardTypes != null && cardTypes.length > 0 && paymentCardType != null) {
            if (Arrays.stream(cardTypes).noneMatch(type -> type.equalsIgnoreCase(paymentCardType))) {
                return false;
            }
        }
        
        return true;
    }
} 