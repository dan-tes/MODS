package Work;

import javax.swing.*;

class SpinnerNumberModelDefault extends SpinnerNumberModel {
    private final int default_val;

    public int getDefault_val() {
        return default_val;
    }

    SpinnerNumberModelDefault(int val, int min, int max, int step, int default_val) {
        super(val, min, max, step);
        this.default_val = default_val;
    }
}