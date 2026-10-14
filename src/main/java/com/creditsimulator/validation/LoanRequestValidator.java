package com.creditsimulator.validation;

import com.creditsimulator.model.LoanRequest;
import java.time.Clock;
import java.util.ArrayList;
import java.util.List;

public class LoanRequestValidator {

    private final List<ValidationRule> rules;

    public LoanRequestValidator(List<ValidationRule> rules) {
        this.rules = List.copyOf(rules);
    }

    public static LoanRequestValidator standard(Clock clock) {
        List<ValidationRule> rules = List.of(
                new VehicleYearRule(clock),
                new LoanAmountRule(),
                new TenorRule(),
                new DownPaymentRule());
        return new LoanRequestValidator(rules);
    }

    public List<String> validate(LoanRequest request) {
        List<String> errors = new ArrayList<>();
        for (ValidationRule rule : rules) {
            rule.check(request).ifPresent(errors::add);
        }
        return errors;
    }

    public void validateOrThrow(LoanRequest request) {
        List<String> errors = validate(request);
        if (!errors.isEmpty()) {
            throw new ValidationException(errors);
        }
    }
}