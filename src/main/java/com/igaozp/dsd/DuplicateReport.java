package com.igaozp.dsd;

public class DuplicateReport {
    private final int duplicateCount;

    public DuplicateReport(int duplicateCount) {
        this.duplicateCount = duplicateCount;
    }

    public boolean hasDuplicates() {
        return duplicateCount > 0;
    }

    public int getDuplicateCount() {
        return duplicateCount;
    }
}
