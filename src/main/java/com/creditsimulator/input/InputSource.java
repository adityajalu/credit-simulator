package com.creditsimulator.input;

public interface InputSource {
    /** Membaca satu baris; mengembalikan null kalau input sudah habis. */
    String nextLine();
}