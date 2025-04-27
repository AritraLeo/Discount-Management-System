package com.discounts.discounts.integration;

import com.discounts.discounts.config.TestDataConfig;
import com.discounts.discounts.model.DiscountedPrice;
import com.discounts.discounts.service.DiscountService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
public class DiscountServiceIntegrationTest {

    @Autowired
    private DiscountService discountService;
    
    @Autowired
    private TestDataConfig.TestData testData;
    
    @Test
    void testCompleteDiscountFlow() {
        // Calculate discounts with payment info
        DiscountedPrice result = discountService.calculateCartDiscounts(
                testData.getCartItems(), 
                testData.getCustomer(),
                Optional.of(testData.getPaymentInfo())
        );
        
        // Check the expected values
        assertEquals(new BigDecimal("2000.00"), result.getOriginalPrice());
        assertEquals(new BigDecimal("900.00"), result.getFinalPrice());
        assertEquals(3, result.getAppliedDiscounts().size());
        
        // Validate discount code
        boolean isValidCode = discountService.validateDiscountCode(
                "SUPER69",
                testData.getCartItems(),
                testData.getCustomer()
        );
        assertTrue(isValidCode);
        
        // Validate invalid code
        boolean isInvalidCode = discountService.validateDiscountCode(
                "INVALID",
                testData.getCartItems(),
                testData.getCustomer()
        );
        assertFalse(isInvalidCode);
    }
    
    @Test
    void testEdgeCases() {
        // Empty cart
        DiscountedPrice emptyCartResult = discountService.calculateCartDiscounts(
                Collections.emptyList(),
                testData.getCustomer(),
                Optional.of(testData.getPaymentInfo())
        );
        
        // For empty cart, we expect zero original price and zero final price
        assertEquals(BigDecimal.ZERO, emptyCartResult.getOriginalPrice());
        assertEquals(BigDecimal.ZERO, emptyCartResult.getFinalPrice());
        assertEquals(0, emptyCartResult.getAppliedDiscounts().size());
        
        // Calculate without payment info
        DiscountedPrice noPaymentResult = discountService.calculateCartDiscounts(
                testData.getCartItems(),
                testData.getCustomer(),
                Optional.empty()
        );
        
        // No bank discount should be applied
        assertEquals(new BigDecimal("2000.00"), noPaymentResult.getOriginalPrice());
        
        // The calculation here should match our service implementation:
        // Original price: 2 * 1000 = 2000
        // After 40% PUMA discount: 2000 - 800 = 1200
        // After 10% T-shirt discount: 1200 - 200 = 1000
        // (No bank discount)
        assertEquals(new BigDecimal("1000.00"), noPaymentResult.getFinalPrice());
        assertEquals(2, noPaymentResult.getAppliedDiscounts().size());
    }
} 