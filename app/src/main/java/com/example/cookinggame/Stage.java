package com.example.cookinggame;

/**
 * The parts of a cooking session. Each recipe step belongs to ASSEMBLY or COOKING;
 * RESULTS is shown after the last step, and leads back to the recipe book.
 */
public enum Stage {
    ASSEMBLY("ASSEMBLY"),
    COOKING("COOKING"),
    RESULTS("RESULTS");

    public final String label;

    Stage(String label) {
        this.label = label;
    }
}
