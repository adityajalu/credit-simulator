package com.creditsimulator.input;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.io.UncheckedIOException;

public class ReaderInputSource implements InputSource {

    private final BufferedReader reader;

    public ReaderInputSource(Reader reader) {
        this.reader = new BufferedReader(reader);
    }

    @Override
    public String nextLine() {
        try {
            String line = reader.readLine();
            // Notepad Windows kadang menyisipkan karakter BOM di awal file
            return line == null ? null : line.replace("\uFEFF", "");
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }
}