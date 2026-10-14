package com.creditsimulator.validation;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.creditsimulator.model.LoanRequest;
import com.creditsimulator.model.VehicleCondition;
import com.creditsimulator.model.VehicleType;
import java.math.BigDecimal;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import org.junit.jupiter.api.Test;

class LoanRequestValidatorTest {

    // Tahun sekarang dikunci ke 2026 supaya test tidak berubah hasil tiap tahun
    private final Clock clock = Clock.fixed(Instant.parse("2026-10-14T00:00:00Z"), ZoneOffset.UTC);
    private final LoanRequestValidator validator = LoanRequestValidator.standard(clock);

    private static LoanRequest request(VehicleCondition condition, int year,
                                       String loan, int tenor, String dp) {
        return new LoanRequest(VehicleType.MOBIL, condition, year,
                new BigDecimal(loan), tenor, new BigDecimal(dp));
    }

    @Test
    void apiSampleIsValid() {
        assertTrue(validator.validate(
                request(VehicleCondition.BARU, 2025, "1000000000", 6, "500000000")).isEmpty());
    }

    @Test
    void newVehicleOlderThanLastYearIsRejected() {
        assertEquals(1, validator.validate(
                request(VehicleCondition.BARU, 2024, "100000000", 3, "50000000")).size());
    }

    @Test
    void usedVehicleMayBeOld() {
        assertTrue(validator.validate(
                request(VehicleCondition.BEKAS, 2010, "100000000", 3, "25000000")).isEmpty());
    }

    @Test
    void yearMustHaveFourDigits() {
        assertEquals(1, validator.validate(
                request(VehicleCondition.BEKAS, 99, "100000000", 3, "25000000")).size());
    }

    @Test
    void tenorMustBeBetweenOneAndSix() {
        assertEquals(1, validator.validate(
                request(VehicleCondition.BEKAS, 2020, "100000000", 7, "25000000")).size());
        assertEquals(1, validator.validate(
                request(VehicleCondition.BEKAS, 2020, "100000000", 0, "25000000")).size());
    }

    @Test
    void loanAboveOneBillionIsRejected() {
        assertEquals(1, validator.validate(
                request(VehicleCondition.BEKAS, 2020, "1000000001", 3, "500000000")).size());
    }

    @Test
    void newVehicleNeedsAtLeast35PercentDownPayment() {
        assertEquals(1, validator.validate(
                request(VehicleCondition.BARU, 2025, "100000000", 3, "34999999")).size());
        assertTrue(validator.validate(
                request(VehicleCondition.BARU, 2025, "100000000", 3, "35000000")).isEmpty());
    }

    @Test
    void usedVehicleNeedsAtLeast25PercentDownPayment() {
        assertEquals(1, validator.validate(
                request(VehicleCondition.BEKAS, 2020, "100000000", 3, "24999999")).size());
        assertTrue(validator.validate(
                request(VehicleCondition.BEKAS, 2020, "100000000", 3, "25000000")).isEmpty());
    }

    @Test
    void downPaymentCannotExceedLoan() {
        assertEquals(1, validator.validate(
                request(VehicleCondition.BEKAS, 2020, "100000000", 3, "100000001")).size());
    }

    @Test
    void allErrorsAreCollectedAtOnce() {
        LoanRequest bad = request(VehicleCondition.BARU, 2000, "100000000", 9, "1");
        assertEquals(3, validator.validate(bad).size());
        ValidationException ex = assertThrows(ValidationException.class,
                () -> validator.validateOrThrow(bad));
        assertEquals(3, ex.getErrors().size());
    }
}