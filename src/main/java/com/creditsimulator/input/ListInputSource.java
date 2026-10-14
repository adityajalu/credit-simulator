package com.creditsimulator.input;

import java.util.Iterator;
import java.util.List;

public class ListInputSource implements InputSource {

    private final Iterator<String> lines;

    public ListInputSource(List<String> lines) {
        this.lines = List.copyOf(lines).iterator();
    }

    @Override
    public String nextLine() {
        return lines.hasNext() ? lines.next() : null;
    }
}