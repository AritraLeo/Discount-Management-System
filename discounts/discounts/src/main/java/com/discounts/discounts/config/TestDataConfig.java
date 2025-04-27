package com.discounts.discounts.config;

import com.discounts.discounts.model.BrandTier;
import com.discounts.discounts.model.CartItem;
import com.discounts.discounts.model.CustomerProfile;
import com.discounts.discounts.model.PaymentInfo;
import com.discounts.discounts.model.Product;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.math.BigDecimal;
import java.util.List;

@Configuration
public class TestDataConfig {
    
    @Bean
    public TestData testData() {
        // Create test data
        TestData testData = new TestData();
        
        // Create a PUMA T-shirt product
        Product pumaShirt = Product.builder()
                .id("prod1")
                .brand("PUMA")
                .brandTier(BrandTier.REGULAR)
                .category("T-shirts")
                .basePrice(new BigDecimal("1000.00"))
                .currentPrice(new BigDecimal("1000.00"))  // Will be calculated after discounts
                .build();
        
        // Create cart item
        CartItem cartItem = CartItem.builder()
                .product(pumaShirt)
                .quantity(2)
                .size("M")
                .build();
        
        // Create customer profile
        CustomerProfile customer = CustomerProfile.builder()
                .id("cust1")
                .tier("REGULAR")
                .build();
        
        // Create payment info for ICICI Bank
        PaymentInfo paymentInfo = PaymentInfo.builder()
                .method("CARD")
                .bankName("ICICI")
                .cardType("CREDIT")
                .build();
        
        // Add to test data
        testData.setCartItems(List.of(cartItem));
        testData.setCustomer(customer);
        testData.setPaymentInfo(paymentInfo);
        
        return testData;
    }
    
    public static class TestData {
        private List<CartItem> cartItems;
        private CustomerProfile customer;
        private PaymentInfo paymentInfo;
        
        public List<CartItem> getCartItems() {
            return cartItems;
        }
        
        public void setCartItems(List<CartItem> cartItems) {
            this.cartItems = cartItems;
        }
        
        public CustomerProfile getCustomer() {
            return customer;
        }
        
        public void setCustomer(CustomerProfile customer) {
            this.customer = customer;
        }
        
        public PaymentInfo getPaymentInfo() {
            return paymentInfo;
        }
        
        public void setPaymentInfo(PaymentInfo paymentInfo) {
            this.paymentInfo = paymentInfo;
        }
    }
} 