# Discount Management System

This is a Spring Boot application that implements a discount service for an e-commerce website. It handles various types of discounts:

- Brand-specific discounts (e.g., "Min 40% off on PUMA")
- Bank card offers (e.g., "10% instant discount on ICICI Bank cards")
- Category-specific deals (e.g., "Extra 10% off on T-shirts")
- Vouchers (e.g., 'SUPER69' for 69% off on any product)

## Setup and Running

1. Make sure you have JDK 17+ installed
2. Clone this repository
3. Configure PostgreSQL in `application.properties` (or use the default configuration)
4. Run the application:
   ```
   ./gradlew bootRun
   ```
   (Use `gradlew.bat bootRun` on Windows)

## Testing the Application

You can test the application using:

1. Unit tests:

   ```
   ./gradlew test
   ```

2. API endpoints:
   - Calculate discounts: `GET /api/discounts/calculate?usePaymentInfo=true`
   - Validate discount code: `GET /api/discounts/validate?code=SUPER69`

## Implementation Details

The discount calculation follows this order:

1. First apply brand/category discounts
2. Then apply coupon codes
3. Then apply bank offers

The discount validation handles:

- Brand exclusions
- Category restrictions
- Customer tier requirements

## Sample Scenario

The system includes a test scenario with:

- PUMA T-shirt with "Min 40% off" (40% discount)
- Additional 10% off on T-shirts category
- ICICI bank offer of 10% instant discount

Final calculation:

- Original price: ₹2000 (₹1000 x 2 quantity)
- After PUMA discount (40%): ₹1200
- After T-shirt category discount (10%): ₹1000
- After ICICI bank discount (10%): ₹900
