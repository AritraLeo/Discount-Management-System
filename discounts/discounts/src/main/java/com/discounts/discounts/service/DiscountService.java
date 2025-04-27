package com.discounts.discounts.service;

import com.discounts.discounts.exception.DiscountCalculationException;
import com.discounts.discounts.exception.DiscountValidationException;
import com.discounts.discounts.model.CartItem;
import com.discounts.discounts.model.CustomerProfile;
import com.discounts.discounts.model.DiscountedPrice;
import com.discounts.discounts.model.PaymentInfo;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public interface DiscountService {
   /**
    * Calculate final price after applying discount logic:
    * - First apply brand/category discounts
    * - Then apply coupon codes
    * - Then apply bank offers
    *
    * @param cartItems List of items in the cart
    * @param customer Customer profile information
    * @param paymentInfo Optional payment information
    * @return Calculated discounted price details
    * @throws DiscountCalculationException if calculation fails
    */
   DiscountedPrice calculateCartDiscounts(
       List<CartItem> cartItems,
       CustomerProfile customer,
       Optional<PaymentInfo> paymentInfo
   ) throws DiscountCalculationException;

   /**
    * Validate if a discount code can be applied.
    * Handle specific cases like:
    * - Brand exclusions
    * - Category restrictions
    * - Customer tier requirements
    *
    * @param code Discount code to validate
    * @param cartItems Current cart items
    * @param customer Customer profile
    * @return true if code is valid, false otherwise
    * @throws DiscountValidationException if validation fails
    */
   boolean validateDiscountCode(
       String code,
       List<CartItem> cartItems,
       CustomerProfile customer
   ) throws DiscountValidationException;
} 