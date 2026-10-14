package com.creditsimulator.rate;

import java.math.BigDecimal;

public class StepUpInterestRatePolicy implements InterestRatePolicy {

    private static final BigDecimal EVEN_YEAR_STEP = new BigDecimal("0.001");
    private static final BigDecimal ODD_YEAR_STEP = new BigDecimal("0.005");

    private final BigDecimal baseRate;

    public StepUpInterestRatePolicy(BigDecimal baseRate) {
        this.baseRate = baseRate;
    }

    @Override
    public BigDecimal rateForYear(int year) {
        if ( year < 1) {
            throw new IllegalArgumentException("Tahun harus >= 1");
        }
        BigDecimal rate = baseRate;
        for (int y = 2; y <= year; y++) {
            rate = rate.add(y % 2 == 0 ? EVEN_YEAR_STEP : ODD_YEAR_STEP);
        }
      return rate;
    }

}
