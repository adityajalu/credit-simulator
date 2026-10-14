package com.creditsimulator.validation;

import java.util.Optional;

import com.creditsimulator.model.LoanRequest;

public class TenorRule implements ValidationRule {

    private static final int MIN_TENOR = 1;
    private static final int MAX_TENOR = 6;

    @Override
    public Optional<String> check(LoanRequest request) {
        int tenor = request.tenorYears();
        if (tenor < MIN_TENOR || tenor > MAX_TENOR) {
            return Optional.of("Tenor harus antara 1 sampai 6 tahun");
        }
        return Optional.empty();
    }
}