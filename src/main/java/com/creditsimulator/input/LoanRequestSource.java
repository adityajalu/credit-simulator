package com.creditsimulator.input;

import com.creditsimulator.model.LoanRequest;

public interface LoanRequestSource {
    LoanRequest read();
}