package com.creditsimulator.rate;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.math.BigDecimal;

import org.junit.jupiter.api.Test;

import com.creditsimulator.model.VehicleType;

public class InterestRateFactoryTest {
    @Test
    void carRateStepsUpAlternately() {
        InterestRatePolicy policy = InterestRateFactory.forVehicle(VehicleType.MOBIL);
        String[] expected = { "0.080", "0.081", "0.086", "0.087", "0.092", "0.093" };
        for (int year = 1; year <= 6; year++) {
            assertEquals(0, new BigDecimal(expected[year - 1]).compareTo(policy.rateForYear(year)),
                    "tahun " + year);
        }
    }

    @Test
    void motorStartsAtNinePercent() {
        InterestRatePolicy policy = InterestRateFactory.forVehicle(VehicleType.MOTOR);
        assertEquals(0, new BigDecimal("0.09").compareTo(policy.rateForYear(1)));
    }
}
