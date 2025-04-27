package com.discounts.discounts.service.impl;

import com.discounts.discounts.exception.DiscountCalculationException;
import com.discounts.discounts.exception.DiscountValidationException;
import com.discounts.discounts.model.CartItem;
import com.discounts.discounts.model.CustomerProfile;
import com.discounts.discounts.model.DiscountedPrice;
import com.discounts.discounts.model.PaymentInfo;
import com.discounts.discounts.model.discount.BankDiscount;
import com.discounts.discounts.model.discount.BrandDiscount;
import com.discounts.discounts.model.discount.CategoryDiscount;
import com.discounts.discounts.model.discount.VoucherDiscount;
import com.discounts.discounts.repository.DiscountRepository;
import com.discounts.discounts.service.DiscountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class DiscountServiceImpl implements DiscountService {

    @Autowired
    private DiscountRepository discountRepository;

    @Override
    public DiscountedPrice calculateCartDiscounts(
            List<CartItem> cartItems,
            CustomerProfile customer,
            Optional<PaymentInfo> paymentInfo
    ) throws DiscountCalculationException {
        try {
            // Handle empty cart case
            if (cartItems.isEmpty()) {
                return DiscountedPrice.builder()
                        .originalPrice(BigDecimal.ZERO)
                        .finalPrice(BigDecimal.ZERO)
                        .appliedDiscounts(new HashMap<>())
                        .message("No items in cart")
                        .build();
            }
            
            // Calculate original total price
            BigDecimal originalPrice = calculateOriginalPrice(cartItems);
            
            // Apply brand and category discounts
            Map<String, BigDecimal> appliedDiscounts = new HashMap<>();
            BigDecimal priceAfterProductDiscounts = applyProductDiscounts(cartItems, customer, appliedDiscounts);
            
            // Apply voucher code discounts (assume they're already validated)
            // This would be extended in a real implementation to check for and apply voucher codes
            
            // Apply bank offers if payment info is provided
            BigDecimal finalPrice = priceAfterProductDiscounts;
            if (paymentInfo.isPresent()) {
                finalPrice = applyBankDiscounts(priceAfterProductDiscounts, paymentInfo.get(), appliedDiscounts);
            }
            
            // Build message
            String message = buildDiscountMessage(appliedDiscounts);
            
            // Return the calculated discounted price
            return DiscountedPrice.builder()
                    .originalPrice(originalPrice)
                    .finalPrice(finalPrice)
                    .appliedDiscounts(appliedDiscounts)
                    .message(message)
                    .build();
            
        } catch (Exception e) {
            throw new DiscountCalculationException("Error calculating discounts: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean validateDiscountCode(
            String code,
            List<CartItem> cartItems,
            CustomerProfile customer
    ) throws DiscountValidationException {
        try {
            // Find the voucher discount with this code
            Optional<VoucherDiscount> voucherOpt = discountRepository.findVoucherByCode(code);
            
            if (voucherOpt.isEmpty() || !voucherOpt.get().isActive()) {
                return false;
            }
            
            VoucherDiscount voucher = voucherOpt.get();
            
            // Check if the voucher is applicable to all items in the cart
            for (CartItem item : cartItems) {
                String category = item.getProduct().getCategory();
                String brand = item.getProduct().getBrand();
                
                if (!voucher.isApplicable(category, brand, item.getProduct().getBrandTier(), customer.getTier())) {
                    return false;
                }
            }
            
            return true;
        } catch (Exception e) {
            throw new DiscountValidationException("Error validating discount code: " + e.getMessage(), e);
        }
    }
    
    private BigDecimal calculateOriginalPrice(List<CartItem> cartItems) {
        return cartItems.stream()
                .map(item -> item.getProduct().getBasePrice().multiply(BigDecimal.valueOf(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
    
    private BigDecimal applyProductDiscounts(
            List<CartItem> cartItems,
            CustomerProfile customer,
            Map<String, BigDecimal> appliedDiscounts
    ) {
        BigDecimal totalAfterDiscount = BigDecimal.ZERO;
        
        for (CartItem item : cartItems) {
            BigDecimal itemTotal = item.getProduct().getBasePrice().multiply(BigDecimal.valueOf(item.getQuantity()));
            BigDecimal itemDiscountedPrice = itemTotal;
            
            // Apply brand discounts
            List<BrandDiscount> brandDiscounts = discountRepository.findActiveBrandDiscounts();
            for (BrandDiscount discount : brandDiscounts) {
                if (discount.isApplicable(
                        item.getProduct().getCategory(),
                        item.getProduct().getBrand(),
                        item.getProduct().getBrandTier(),
                        customer.getTier()
                )) {
                    BigDecimal discountAmount;
                    if (discount.getDiscountPercentage() != null) {
                        discountAmount = itemTotal.multiply(discount.getDiscountPercentage())
                                .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
                    } else {
                        discountAmount = discount.getDiscountAmount().multiply(BigDecimal.valueOf(item.getQuantity()));
                    }
                    
                    itemDiscountedPrice = itemDiscountedPrice.subtract(discountAmount);
                    appliedDiscounts.put(discount.getName(), 
                            appliedDiscounts.getOrDefault(discount.getName(), BigDecimal.ZERO).add(discountAmount));
                }
            }
            
            // Apply category discounts
            List<CategoryDiscount> categoryDiscounts = discountRepository.findActiveCategoryDiscounts();
            for (CategoryDiscount discount : categoryDiscounts) {
                if (discount.isApplicable(
                        item.getProduct().getCategory(),
                        item.getProduct().getBrand(),
                        item.getProduct().getBrandTier(),
                        customer.getTier()
                )) {
                    BigDecimal discountAmount;
                    if (discount.getDiscountPercentage() != null) {
                        discountAmount = itemTotal.multiply(discount.getDiscountPercentage())
                                .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
                    } else {
                        discountAmount = discount.getDiscountAmount().multiply(BigDecimal.valueOf(item.getQuantity()));
                    }
                    
                    itemDiscountedPrice = itemDiscountedPrice.subtract(discountAmount);
                    appliedDiscounts.put(discount.getName(), 
                            appliedDiscounts.getOrDefault(discount.getName(), BigDecimal.ZERO).add(discountAmount));
                }
            }
            
            totalAfterDiscount = totalAfterDiscount.add(itemDiscountedPrice);
        }
        
        return totalAfterDiscount;
    }
    
    private BigDecimal applyBankDiscounts(
            BigDecimal priceAfterProductDiscounts,
            PaymentInfo paymentInfo,
            Map<String, BigDecimal> appliedDiscounts
    ) {
        BigDecimal finalPrice = priceAfterProductDiscounts;
        
        // Only apply bank discounts for card payments
        if ("CARD".equalsIgnoreCase(paymentInfo.getMethod()) && paymentInfo.getBankName() != null) {
            List<BankDiscount> bankDiscounts = discountRepository.findActiveBankDiscounts();
            
            for (BankDiscount discount : bankDiscounts) {
                if (discount.isApplicableForPayment(paymentInfo.getBankName(), paymentInfo.getCardType())) {
                    BigDecimal discountAmount;
                    if (discount.getDiscountPercentage() != null) {
                        discountAmount = priceAfterProductDiscounts.multiply(discount.getDiscountPercentage())
                                .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
                    } else {
                        discountAmount = discount.getDiscountAmount();
                    }
                    
                    finalPrice = finalPrice.subtract(discountAmount);
                    appliedDiscounts.put(discount.getName(), 
                            appliedDiscounts.getOrDefault(discount.getName(), BigDecimal.ZERO).add(discountAmount));
                }
            }
        }
        
        return finalPrice;
    }
    
    private String buildDiscountMessage(Map<String, BigDecimal> appliedDiscounts) {
        if (appliedDiscounts.isEmpty()) {
            return "No discounts applied";
        }
        
        return "Applied discounts: " + 
                appliedDiscounts.entrySet().stream()
                        .map(entry -> entry.getKey() + ": ₹" + entry.getValue())
                        .collect(Collectors.joining(", "));
    }
} 