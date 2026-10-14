package com.creditsimulator.rate;

import java.math.BigDecimal;

public interface InterestRatePolicy {
    /*
     * kembalikan suku bunga ke pecahan, semisal dari 0.08 ke 8%
     */

    BigDecimal rateForYear(int year);
}
