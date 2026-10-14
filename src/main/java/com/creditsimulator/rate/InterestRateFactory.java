package com.creditsimulator.rate;

import java.math.BigDecimal;

import com.creditsimulator.model.VehicleType;

public final class InterestRateFactory {
    private InterestRateFactory() {
    }

    public static InterestRatePolicy forVehicle(VehicleType type) {
        return switch (type) {
            case MOBIL -> new StepUpInterestRatePolicy(new BigDecimal("0.08"));
            case MOTOR -> new StepUpInterestRatePolicy(new BigDecimal("0.09"));
        };
    }
}
