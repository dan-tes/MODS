package Physics;

import static Settings.Height;

public class Point {
    double x, y;
    public Point(double x, double y) {
        this.x = x;
        this.y = y;
    }
    public double getX() {
        return x;
    }

    public void setX(double x) {
        this.x = x;
    }

    public double getY() {
        return y;
    }

    public void setY(double y) {
        this.y = y;
    }
    public static Point changing_coordinate_system(double x, double y){
        int X = (int) x + 20;
        int Y = (int) (Height - y - 55);

        return new Point(X, Y);
    }

}
