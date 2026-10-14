
package com.creditsimulator.validation;

import java.math.BigDecimal;
import java.util.Optional;

import com.creditsimulator.model.LoanRequest;

public class LoanAmountRule implements ValidationRule {
    private static final BigDecimal MAX_LOAN = new BigDecimal("1000000000");

    @Override
    public Optional<String> check(LoanRequest request) {
        BigDecimal amount = request.totalLoanAmount();
        if (amount.signum() <= 0) {
            return Optional.of("Jumlah pinjaman harus lebih dari 0");
        }
        if (amount.compareTo(MAX_LOAN) > 0) {
            return Optional.of("Jumlah pinjaman maksimal 1 miliar");
        }
        return Optional.empty();
    }
}