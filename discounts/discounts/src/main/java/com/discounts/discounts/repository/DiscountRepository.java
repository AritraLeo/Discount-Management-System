package com.discounts.discounts.repository;

import com.discounts.discounts.model.discount.BankDiscount;
import com.discounts.discounts.model.discount.BrandDiscount;
import com.discounts.discounts.model.discount.CategoryDiscount;
import com.discounts.discounts.model.discount.VoucherDiscount;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DiscountRepository {
    List<BrandDiscount> findActiveBrandDiscounts();
    List<CategoryDiscount> findActiveCategoryDiscounts();
    List<BankDiscount> findActiveBankDiscounts();
    Optional<VoucherDiscount> findVoucherByCode(String code);
} 