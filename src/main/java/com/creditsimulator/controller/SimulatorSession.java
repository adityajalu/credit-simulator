package com.creditsimulator.controller;

import com.creditsimulator.calculator.InstallmentCalculator;
import com.creditsimulator.input.InputException;
import com.creditsimulator.input.LoanRequestSource;
import com.creditsimulator.model.Calculation;
import com.creditsimulator.model.LoanRequest;
import com.creditsimulator.validation.LoanRequestValidator;
import com.creditsimulator.validation.ValidationException;
import com.creditsimulator.view.ConsoleView;

import java.time.Clock;
import java.util.List;
import java.util.Optional;

/**
 * Satu sesi simulasi kredit: membaca permintaan, memvalidasi,
 * menghitung angsuran, dan menampilkan hasilnya.
 *
 * <p>Mengembalikan {@link Optional} berisi {@link Calculation} jika berhasil,
 * atau {@link Optional#empty()} jika ada error yang sudah ditampilkan ke user.
 */
public class SimulatorSession {

    private final ConsoleView view;
    private final LoanRequestValidator validator;
    private final InstallmentCalculator calculator;
    private final LoanRequestSource source;

    public SimulatorSession(ConsoleView view, Clock clock, LoanRequestSource source) {
        this.view       = view;
        this.validator  = LoanRequestValidator.standard(clock);
        this.calculator = new InstallmentCalculator();
        this.source     = source;
    }

    /**
     * Menjalankan satu siklus simulasi.
     *
     * @return {@link Optional} berisi hasil kalkulasi jika sukses,
     *         atau {@link Optional#empty()} jika ada kesalahan
     */
    public Optional<Calculation> run() {
        try {
            LoanRequest request = source.read();

            List<String> errors = validator.validate(request);
            if (!errors.isEmpty()) {
                view.showErrors(errors);
                return Optional.empty();
            }

            Calculation calc = new Calculation(request, calculator.calculate(request));
            view.showResult(calc);
            return Optional.of(calc);

        } catch (ValidationException e) {
            view.showErrors(e.getErrors());
            return Optional.empty();
        } catch (InputException e) {
            view.showError(e.getMessage());
            return Optional.empty();
        }
    }
}
