package Graphics;

import Physics.MechanicalParameters;
import Physics.Point;

import java.util.Date;

public class WorkMain implements Runnable {
    private static final int MAXY = 445;
    private static final int MAXX = 800;

    private int mass = -1;
    private int V = -1;
    private int Vgr = -1;

    public double getVx() {
        return Vx;
    }

    public void setVx(double vx) {
        Vx = vx;
    }

    public double getVy() {
        return Vy;
    }

    public void setVy(double vy) {
        Vy = vy;
    }

    private int A = -1;
    private int Agr = -1;
    private int F = -1;
    private int Fgr = -1;

    private double T = 1000;
    private double Vx = 0, Vy = 0;
    private double Vxo = 0, Vyo = 0;
    private double Ax = 0, Ay = 0;
    private double To = 0;

    private final double delTime = 0.042;
    private double X = 0, Y = 0;
    private double Xo = 0, Yo = 0;

    private Thread thread;
    private boolean running = true;

    private GraphicsEngine graphicsEngine;

    public double getX() {
        return X;
    }

    public double getY() {
        return Y;
    }

    public WorkMain() {
        graphicsEngine = new GraphicsEngine(this);
    }

    // Сеттеры
    public void setX(int x) { X = x; }
    public void setY(int y) { Y = y; }
    public void setA(int a, int agr) { A = a; Agr = agr; }
    public void setT(int t) { T = t; }
    public void setMass(int mass) { this.mass = mass; }
    public void setF(int f, int fgr) { F = f; Fgr = fgr; }
    public void setV(int v, int vgr) { V = v; Vgr = vgr; }

    public boolean startModeling() {
        if (V == -1 && (A == -1 && (F == -1 || mass == -1))) {
            reset();
            return false;
        }

        if (A == -1 && F != -1 && mass != -1) {
            A = F / mass;
            Agr = Fgr;
        }

        if (V != -1) {
            Vx = Math.cos(Math.toRadians(Vgr)) * V;
            Vy = Math.sin(Math.toRadians(Vgr)) * V;
        }

        if (A != -1) {
            Ax = Math.cos(Math.toRadians(Agr)) * A;
            Ay = Math.sin(Math.toRadians(Agr)) * A;
        }

        running = true;
        Xo = X; Yo = Y;
        Vxo = Vx; Vyo = Vy;
        T /= 1000;
        To = T;

        graphicsEngine.render(new MechanicalParameters((int) X, (int) Y, V, Vgr));
        thread = new Thread(this);
        thread.start();
        return true;
    }

    public Point pause() {
        double x = X, y = Y;
        stop();
        X = x;
        Y = y;
        return new Point(x, y);
    }

    @Override
    public void run() {
        while (running) {
            Date date = new Date();
            long ti = date.getTime();

            X = (permutationCoordinatesX() * T) * delTime + X;
            Y = (permutationCoordinatesY() * T) * delTime + Y;

            if (X < 0 || Y < 0 || X > MAXX || Y > MAXY) {
                X = Xo;
                Y = Yo;
                T = To;
                Vx = Vxo;
                Vy = Vyo;
            }

            V = (int) Math.sqrt(Vx * Vx + Vy * Vy);
            Vgr = (int) Math.toDegrees(Math.atan2(Vy, Vx));
            if (Vy < 0) Vgr = 360 + Vgr;

            graphicsEngine.render(new MechanicalParameters((int) X, (int) Y, V, Vgr));

            if (ti + delTime * 1000 > date.getTime()) {
                try {
                    Thread.sleep((long) (ti + delTime * 1000 - date.getTime()));
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void stop() {
        running = false;
    }

    public void reset() {
        stop();
        mass = -1; A = -1; Agr = -1;
        F = -1; Fgr = -1;
        V = -1; Vgr = -1;
        T = 1000;
        X = 0; Y = 0;
    }

    private double permutationCoordinatesX() {
        if (A < 0) return Vx;
        double v = Vx;
        Vx += Ax * T;
        return v;
    }

    private double permutationCoordinatesY() {
        if (A < 0) return Vy;
        double v = Vy;
        Vy += Ay * T;
        return v;
    }

    public double getAx() {
        return Ax;
    }

    public void setAx(double ax) {
        Ax = ax;
    }

    public double getAy() {
        return Ay;
    }

    public void setAy(double ay) {
        Ay = ay;
    }
}
