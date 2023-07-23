package com.example.doan;

import android.text.InputFilter;
import android.text.Spanned;

public class InputFilterMinMax implements InputFilter {
    private long min, max;

    public InputFilterMinMax(long min, long max) {
        this.min = min;
        this.max = max;
    }

    @Override
    public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
        try {
            String input = dest.subSequence(0, dstart) + source.toString() + dest.subSequence(dend, dest.length());
            long value = Long.parseLong(input);
            if (value >= min && value <= max) {
                return null;
            }
        } catch (NumberFormatException e) {
            // do nothing
        }
        return "";
    }
}
