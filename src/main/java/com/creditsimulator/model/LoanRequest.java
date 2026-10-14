package com.creditsimulator.model;

import java.math.BigDecimal;

public record LoanRequest(
        VehicleType vehicleType,
        VehicleCondition vehicleCondition,
        int vehicleYear,
        BigDecimal totalLoanAmount,
        int tenorYears,
        BigDecimal downPayment) {
    public BigDecimal principal() {
        return totalLoanAmount.subtract(downPayment);
    }

}
