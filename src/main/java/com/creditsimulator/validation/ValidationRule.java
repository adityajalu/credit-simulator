package com.creditsimulator.validation;

import java.util.Optional;

import com.creditsimulator.model.LoanRequest;

public interface ValidationRule {
    // kembalikan pesan error ketika data empty
    Optional<String> check(LoanRequest request);
}
