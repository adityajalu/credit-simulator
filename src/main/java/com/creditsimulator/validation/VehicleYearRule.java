package com.creditsimulator.validation;

import java.time.Clock;
import java.time.Year;
import java.util.Optional;

import com.creditsimulator.model.LoanRequest;
import com.creditsimulator.model.VehicleCondition;

public class VehicleYearRule implements ValidationRule {
    private final Clock clock;

    public VehicleYearRule(Clock clock) {
        this.clock = clock;
    }

     @Override
    public Optional<String> check(LoanRequest request) {
        int year = request.vehicleYear();
        if (year < 1000 || year > 9999) {
            return Optional.of("Tahun kendaraan harus angka 4 digit");
        }
        int minimumNewYear = Year.now(clock).getValue() - 1;
        if (request.vehicleCondition() == VehicleCondition.BARU && year < minimumNewYear) {
            return Optional.of("Kendaraan baru tidak boleh bertahun kurang dari " + minimumNewYear);
        }
        return Optional.empty();
    }
}