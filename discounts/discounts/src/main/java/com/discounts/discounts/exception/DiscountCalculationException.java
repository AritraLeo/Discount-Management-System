package com.discounts.discounts.exception;

public class DiscountCalculationException extends RuntimeException {
   public DiscountCalculationException(String message) {
       super(message);
   }
   
   public DiscountCalculationException(String message, Throwable cause) {
       super(message, cause);
   }
} 