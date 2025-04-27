package com.discounts.discounts.service;

import com.discounts.discounts.model.BrandTier;
import com.discounts.discounts.model.CartItem;
import com.discounts.discounts.model.CustomerProfile;
import com.discounts.discounts.model.DiscountedPrice;
import com.discounts.discounts.model.PaymentInfo;
import com.discounts.discounts.model.Product;
import com.discounts.discounts.model.discount.BankDiscount;
import com.discounts.discounts.model.discount.BrandDiscount;
import com.discounts.discounts.model.discount.CategoryDiscount;
import com.discounts.discounts.model.discount.VoucherDiscount;
import com.discounts.discounts.repository.DiscountRepository;
import com.discounts.discounts.service.impl.DiscountServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class DiscountServiceImplTest {

    @Mock
    private DiscountRepository discountRepository;

    @InjectMocks
    private DiscountServiceImpl discountService;

    private Product pumaShirt;
    private CartItem cartItem;
    private CustomerProfile customer;
    private PaymentInfo paymentInfo;
    private List<CartItem> cartItems;

    @BeforeEach
    void setUp() {
        // Set up test data
        pumaShirt = Product.builder()
                .id("prod1")
                .brand("PUMA")
                .brandTier(BrandTier.REGULAR)
                .category("T-shirts")
                .basePrice(new BigDecimal("1000.00"))
                .currentPrice(new BigDecimal("1000.00"))
                .build();

        cartItem = CartItem.builder()
                .product(pumaShirt)
                .quantity(2)
                .size("M")
                .build();

        cartItems = new ArrayList<>();
        cartItems.add(cartItem);

        customer = CustomerProfile.builder()
                .id("cust1")
                .tier("REGULAR")
                .build();

        paymentInfo = PaymentInfo.builder()
                .method("CARD")
                .bankName("ICICI")
                .cardType("CREDIT")
                .build();
    }

    @Test
    void testCalculateCartDiscounts_WithAllDiscounts() {
        // Set up mocks needed for this test
        setupBrandDiscount();
        setupCategoryDiscount();
        setupBankDiscount();
        
        // Test with all discounts (brand, category, bank)
        DiscountedPrice result = discountService.calculateCartDiscounts(cartItems, customer, Optional.of(paymentInfo));

        // Original price: 2 * 1000 = 2000
        // After 40% PUMA discount: 2000 - 800 = 1200
        // After 10% T-shirt discount: 1200 - 200 = 1000
        // After 10% ICICI discount: 1000 - 100 = 900
        assertEquals(new BigDecimal("2000.00"), result.getOriginalPrice());
        assertEquals(new BigDecimal("900.00"), result.getFinalPrice());
        assertEquals(3, result.getAppliedDiscounts().size());
    }

    @Test
    void testCalculateCartDiscounts_WithoutBankDiscount() {
        // Set up only the mocks needed for this test
        setupBrandDiscount();
        setupCategoryDiscount();
        
        // Test without bank discount
        DiscountedPrice result = discountService.calculateCartDiscounts(cartItems, customer, Optional.empty());

        // Original price: 2 * 1000 = 2000
        // After 40% PUMA discount: 2000 - 800 = 1200
        // After 10% T-shirt discount: 1200 - 200 = 1000
        assertEquals(new BigDecimal("2000.00"), result.getOriginalPrice());
        assertEquals(new BigDecimal("1000.00"), result.getFinalPrice());
        assertEquals(2, result.getAppliedDiscounts().size());
    }

    @Test
    void testValidateDiscountCode_ValidCode() {
        // Set up mock only for this test
        when(discountRepository.findVoucherByCode("SUPER69")).thenReturn(Optional.of(
            VoucherDiscount.builder()
                .id("vd1")
                .name("SUPER69 Offer")
                .description("Get 69% off with code SUPER69")
                .discountPercentage(new BigDecimal("69"))
                .isActive(true)
                .code("SUPER69")
                .isOneTimeUse(false)
                .build()
        ));
        
        boolean isValid = discountService.validateDiscountCode("SUPER69", cartItems, customer);
        assertTrue(isValid);
    }

    @Test
    void testValidateDiscountCode_InvalidCode() {
        // Set up mock only for this test
        when(discountRepository.findVoucherByCode("INVALID")).thenReturn(Optional.empty());
        
        boolean isValid = discountService.validateDiscountCode("INVALID", cartItems, customer);
        assertFalse(isValid);
    }
    
    // Helper methods to set up mocks
    private void setupBrandDiscount() {
        when(discountRepository.findActiveBrandDiscounts()).thenReturn(List.of(
            BrandDiscount.builder()
                .id("bd1")
                .name("Min 40% off on PUMA")
                .description("Get minimum 40% off on all PUMA products")
                .discountPercentage(new BigDecimal("40"))
                .isActive(true)
                .brandName("PUMA")
                .build()
        ));
    }
    
    private void setupCategoryDiscount() {
        when(discountRepository.findActiveCategoryDiscounts()).thenReturn(List.of(
            CategoryDiscount.builder()
                .id("cd1")
                .name("Extra 10% off on T-shirts")
                .description("Get additional 10% off on all T-shirts")
                .discountPercentage(new BigDecimal("10"))
                .isActive(true)
                .categoryName("T-shirts")
                .build()
        ));
    }
    
    private void setupBankDiscount() {
        when(discountRepository.findActiveBankDiscounts()).thenReturn(List.of(
            BankDiscount.builder()
                .id("bd1")
                .name("10% instant discount on ICICI Bank cards")
                .description("Get 10% instant discount when paying with ICICI Bank cards")
                .discountPercentage(new BigDecimal("10"))
                .isActive(true)
                .bankName("ICICI")
                .cardTypes(new String[]{"CREDIT", "DEBIT"})
                .build()
        ));
    }
} 