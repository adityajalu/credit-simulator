package com.creditsimulator.validation;
import com.creditsimulator.model.LoanRequest;
import com.creditsimulator.model.VehicleCondition;
import java.math.BigDecimal;
import java.util.Optional;

public class DownPaymentRule implements ValidationRule {

    private static final BigDecimal NEW_MIN = new BigDecimal("0.35");
    private static final BigDecimal USED_MIN = new BigDecimal("0.25");

    @Override
    public Optional<String> check(LoanRequest request) {
        BigDecimal loan = request.totalLoanAmount();
        BigDecimal dp = request.downPayment();
        if (dp.signum() < 0) {
            return Optional.of("DP tidak boleh negatif");
        }
        if (dp.compareTo(loan) > 0) {
            return Optional.of("DP tidak boleh melebihi jumlah pinjaman");
        }
        boolean isNew = request.vehicleCondition() == VehicleCondition.BARU;
        BigDecimal percent = isNew ? NEW_MIN : USED_MIN;
        if (dp.compareTo(loan.multiply(percent)) < 0) {
            return Optional.of("DP kendaraan " + (isNew ? "baru" : "bekas") + " minimal "
                    + percent.movePointRight(2).stripTrailingZeros().toPlainString()
                    + "% dari jumlah pinjaman");
        }
        return Optional.empty();
    }
}