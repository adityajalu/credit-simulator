package com.creditsimulator.model;

import java.util.List;

public record Calculation(LoanRequest request, List<YearlyInstallment> installments) {

    public Calculation {
        installments = List.copyOf(installments);
    }
}