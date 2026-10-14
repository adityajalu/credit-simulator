package com.creditsimulator.calculator;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

import com.creditsimulator.model.LoanRequest;
import com.creditsimulator.model.YearlyInstallment;
import com.creditsimulator.rate.InterestRateFactory;
import com.creditsimulator.rate.InterestRatePolicy;

public class InstallmentCalculator {
    private static final int MONTHS_PER_YEAR = 12;

    public List<YearlyInstallment> calculate(LoanRequest request) {
        InterestRatePolicy policy = InterestRateFactory.forVehicle(request.vehicleType());
        int totalMonths = request.tenorYears() * MONTHS_PER_YEAR;
        BigDecimal principal = request.principal();
        List<YearlyInstallment> result = new ArrayList<>();

        for (int year = 1; year <= request.tenorYears(); year++) {
            BigDecimal rate = policy.rateForYear(year);
            BigDecimal totalDebt = principal.add(principal.multiply(rate));
            int remainingMonths = totalMonths - (year - 1) * MONTHS_PER_YEAR;

            BigDecimal monthly = totalDebt.divide(
                    BigDecimal.valueOf(remainingMonths), 10, RoundingMode.HALF_UP);
            BigDecimal yearly = monthly.multiply(BigDecimal.valueOf(MONTHS_PER_YEAR));

            result.add(new YearlyInstallment(year, rate, monthly));
            principal = totalDebt.subtract(yearly);
        }
        return result;
    }
}
