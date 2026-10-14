package com.creditsimulator.model;

import java.math.BigDecimal;

public record YearlyInstallment(
        int year, BigDecimal rate, BigDecimal monthlyInstallment) {

}
