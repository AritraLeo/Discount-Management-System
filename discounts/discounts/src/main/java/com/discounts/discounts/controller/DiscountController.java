package com.discounts.discounts.controller;

import com.discounts.discounts.config.TestDataConfig;
import com.discounts.discounts.model.CartItem;
import com.discounts.discounts.model.CustomerProfile;
import com.discounts.discounts.model.DiscountedPrice;
import com.discounts.discounts.model.PaymentInfo;
import com.discounts.discounts.service.DiscountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/discounts")
public class DiscountController {

    @Autowired
    private DiscountService discountService;
    
    @Autowired
    private TestDataConfig.TestData testData;
    
    @GetMapping("/calculate")
    public ResponseEntity<DiscountedPrice> calculateDiscounts(
            @RequestParam(value = "usePaymentInfo", defaultValue = "true") boolean usePaymentInfo) {
        
        List<CartItem> cartItems = testData.getCartItems();
        CustomerProfile customer = testData.getCustomer();
        Optional<PaymentInfo> paymentInfo = usePaymentInfo ? 
                Optional.of(testData.getPaymentInfo()) : Optional.empty();
        
        DiscountedPrice discountedPrice = discountService.calculateCartDiscounts(
                cartItems, customer, paymentInfo);
        
        return ResponseEntity.ok(discountedPrice);
    }
    
    @GetMapping("/validate")
    public ResponseEntity<Boolean> validateDiscountCode(
            @RequestParam("code") String code) {
        
        boolean isValid = discountService.validateDiscountCode(
                code, testData.getCartItems(), testData.getCustomer());
        
        return ResponseEntity.ok(isValid);
    }
} 