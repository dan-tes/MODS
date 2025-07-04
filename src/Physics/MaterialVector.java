package Physics;

import Settings;

public class MaterialVector {
    int x1, y1, x2, y2;

    MaterialVector(int x1, int y1, int x2, int y2) {
        this.x1 = x1;
        this.y1 = y1;
        this.x2 = x2;
        this.y2 = y2;
    }

    public void normalize() {
        double dx = x2 - x1;
        double dy = y2 - y1;
        double length = Math.sqrt(dx * dx + dy * dy);

        if (length == 0) {
            return;
        }

        dx /= length;
        dy /= length;

        x2 = x1 + (int)(dx * Settings.vector_size);
        y2 = y1 + (int)(dy * Settings.vector_size);
    }

}
