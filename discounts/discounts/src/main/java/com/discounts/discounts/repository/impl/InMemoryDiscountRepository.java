package com.discounts.discounts.repository.impl;

import com.discounts.discounts.model.BrandTier;
import com.discounts.discounts.model.discount.BankDiscount;
import com.discounts.discounts.model.discount.BrandDiscount;
import com.discounts.discounts.model.discount.CategoryDiscount;
import com.discounts.discounts.model.discount.VoucherDiscount;
import com.discounts.discounts.repository.DiscountRepository;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
public class InMemoryDiscountRepository implements DiscountRepository {
    private final List<BrandDiscount> brandDiscounts = new ArrayList<>();
    private final List<CategoryDiscount> categoryDiscounts = new ArrayList<>();
    private final List<BankDiscount> bankDiscounts = new ArrayList<>();
    private final List<VoucherDiscount> voucherDiscounts = new ArrayList<>();

    public InMemoryDiscountRepository() {
        initializeTestData();
    }

    private void initializeTestData() {
        // Add brand discount for PUMA (40% off)
        brandDiscounts.add(BrandDiscount.builder()
                .id("bd1")
                .name("Min 40% off on PUMA")
                .description("Get minimum 40% off on all PUMA products")
                .discountPercentage(new BigDecimal("40"))
                .isActive(true)
                .brandName("PUMA")
                .build());

        // Add category discount for T-shirts (10% off)
        categoryDiscounts.add(CategoryDiscount.builder()
                .id("cd1")
                .name("Extra 10% off on T-shirts")
                .description("Get additional 10% off on all T-shirts")
                .discountPercentage(new BigDecimal("10"))
                .isActive(true)
                .categoryName("T-shirts")
                .build());

        // Add bank discount for ICICI Bank (10% off)
        bankDiscounts.add(BankDiscount.builder()
                .id("bd1")
                .name("10% instant discount on ICICI Bank cards")
                .description("Get 10% instant discount when paying with ICICI Bank cards")
                .discountPercentage(new BigDecimal("10"))
                .isActive(true)
                .bankName("ICICI")
                .cardTypes(new String[]{"CREDIT", "DEBIT"})
                .build());

        // Add voucher discount SUPER69
        voucherDiscounts.add(VoucherDiscount.builder()
                .id("vd1")
                .name("SUPER69 Offer")
                .description("Get 69% off with code SUPER69")
                .discountPercentage(new BigDecimal("69"))
                .isActive(true)
                .code("SUPER69")
                .isOneTimeUse(false)
                .build());
    }

    @Override
    public List<BrandDiscount> findActiveBrandDiscounts() {
        return brandDiscounts.stream()
                .filter(BrandDiscount::isActive)
                .collect(Collectors.toList());
    }

    @Override
    public List<CategoryDiscount> findActiveCategoryDiscounts() {
        return categoryDiscounts.stream()
                .filter(CategoryDiscount::isActive)
                .collect(Collectors.toList());
    }

    @Override
    public List<BankDiscount> findActiveBankDiscounts() {
        return bankDiscounts.stream()
                .filter(BankDiscount::isActive)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<VoucherDiscount> findVoucherByCode(String code) {
        return voucherDiscounts.stream()
                .filter(VoucherDiscount::isActive)
                .filter(v -> v.getCode().equalsIgnoreCase(code))
                .findFirst();
    }
} 