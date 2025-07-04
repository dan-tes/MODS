package Physics;

import settings.Settings;

public class MaterialVector {
    double x1, y1, x2, y2;

    public MaterialVector(double x1, double y1, double x2, double y2) {
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

        x2 = x1 + (double)(dx * Settings.vector_size);
        y2 = y1 + (double)(dy * Settings.vector_size);
    }

    public int getX1() {
        return (int) x1;
    }

    public void setX1(double x1) {
        this.x1 = x1;
    }

    public int getY2() {
        return (int) y2;
    }

    public void setY2(double y2) {
        this.y2 = y2;
    }

    public int getX2() {
        return (int) x2;
    }

    public void setX2(double x2) {
        this.x2 = x2;
    }

    public int getY1() {
        return (int) y1;
    }

    public void setY1(double y1) {
        this.y1 = y1;
    }
}
