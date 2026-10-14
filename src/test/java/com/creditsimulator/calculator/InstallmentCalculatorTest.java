package com.creditsimulator.calculator;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

import org.junit.jupiter.api.Test;

import com.creditsimulator.model.LoanRequest;
import com.creditsimulator.model.VehicleCondition;
import com.creditsimulator.model.VehicleType;
import com.creditsimulator.model.YearlyInstallment;

public class InstallmentCalculatorTest {

    private final InstallmentCalculator calculator = new InstallmentCalculator();

    @Test
    void threeYearCar_matchesExcelExample() {
        LoanRequest request = new LoanRequest(VehicleType.MOBIL, VehicleCondition.BARU, 2025,
                new BigDecimal("100000000"), 3, new BigDecimal("25000000"));

        List<YearlyInstallment> result = calculator.calculate(request);

        assertEquals(3, result.size());
        assertMoney("2250000.00", result.get(0).monthlyInstallment());
        assertMoney("2432250.00", result.get(1).monthlyInstallment());
        assertMoney("2641423.50", result.get(2).monthlyInstallment());
    }

    @Test
    void sixYearCar_firstThreeYearsMatchApiSample() {
        LoanRequest request = new LoanRequest(VehicleType.MOBIL, VehicleCondition.BARU, 2025,
                new BigDecimal("1000000000"), 6, new BigDecimal("500000000"));

        List<YearlyInstallment> result = calculator.calculate(request);

        assertEquals(6, result.size());
        assertMoney("7500000.00", result.get(0).monthlyInstallment());
        assertMoney("8107500.00", result.get(1).monthlyInstallment());
        assertMoney("8804745.00", result.get(2).monthlyInstallment());
    }

    private static void assertMoney(String expected, BigDecimal actual) {
        assertEquals(new BigDecimal(expected), actual.setScale(2, RoundingMode.HALF_UP));
    }
}
