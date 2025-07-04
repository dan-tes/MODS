package Work;

import java.util.Date;

public class WorkMain implements  Runnable{
    private static final int MAXY = 445; // Максимольное значение отдаления точки по оси OY
    private static final int MAXX = 800; // Максимольное значение отдаления точки по оси OX
    private int mass  = -1; // масса материальной точки
    private int V  = -1; // скорсть материальной точки
    private int Vgr  = -1; // направление скорости материальной точки
    private int A  = -1; // ускорение материальной точки
    private int Agr  = -1; // направление ускорения материальной точки
    private int F  = -1; // геометрическая сумма сил материальной точки
    private int Fgr = -1; // направление геометрической суммы сил материальной точки
    private double T = 1000; // относительное програмное время
    private double Vx = 0;  // скорсть относительно оси OX
    private double Vy = 0; // скорсть относительно оси OY
    private double Vxo = 0;
    private double Vyo = 0;

    public int getMass() {
        return mass;
    }

    public int getV() {
        return V;
    }

    public void setV(int v) {
        V = v;
    }

    public int getVgr() {
        return Vgr;
    }

    public void setVgr(int vgr) {
        Vgr = vgr;
    }

    public int getA() {
        return A;
    }

    public void setA(int a) {
        A = a;
    }

    public int getAgr() {
        return Agr;
    }

    public void setAgr(int agr) {
        Agr = agr;
    }

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

    public double getVxo() {
        return Vxo;
    }

    public void setVxo(double vxo) {
        Vxo = vxo;
    }

    public double getVyo() {
        return Vyo;
    }

    public void setVyo(double vyo) {
        Vyo = vyo;
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

    private double Ax = 0;// ускорение материальной точки по оси OX
    private double Ay = 0;// ускорение материальной точки по оси OY
    private double To = 0;
    private final double delTime = 0.042;// чатота обновления
    private Thread thread;
    WorkFrame workFrame;
    private double X = 0; // стартовая координата
    private double Y = 0;
    private double Xo = 0;
    private double Yo = 0;
    private boolean running = true;

    public WorkMain(){
        if (Settings.testing) {
            setX(10);
            setY(10);
            setV(150, 45);
            setA(10, 270);
        }
        workFrame = new  WorkFrame(this);
    }
    // Устоновщики значений
    public void setY(int y) {
        Y = y;
    }

    public void setX(int x) {
        X = x;
    }

    public void setA(int a, int agr) {
        A = a;
        Agr = agr;
    }

    public void setT(int t) {
        T = t;
    }

    public void setMass(int mass) {
        this.mass = mass;
    }

    public void setF(int f, int fgr) {
        F = f;
        Fgr = fgr;
    }

    public void setV(int v, int vgr) {
        V = v;
        Vgr = vgr;
    }
    // старт процесса модуляции
    public boolean startModeling() {
        if (V == -1 && (A == -1 && (F == -1 || mass == -1))) { // проверка достаточно ли введёных значений для старта
            mass = -1;
            Vgr = 0;
            Agr = -1;
            F = -1;
            Fgr = -1;
            T = 1000;
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
        if (A != -1){
            Ax = Math.cos(Math.toRadians(Agr)) * A;
            Ay = Math.sin(Math.toRadians(Agr)) * A;
        }
        running = true;
        Xo = X;
        Vxo = Vx;
        Vyo = Vy;
        Yo =  Y;
        T /= 1000;
        To = T;
        workFrame.paint(X, Y);
        thread = new Thread(this);
        thread.start();
        return true;
    }

    @Override
    public void run() {
        while (running){
            Date date = new Date();
            long ti = date.getTime();
            // перерасчёт координат
            X = (permutationCoordinatesX() * T) * delTime + X;
            Y = (permutationCoordinatesY() * T) * delTime + Y;
            if (X < 0 || Y < 0 || X > MAXX || Y > MAXY) { //
                X = Xo;
                Y = Yo;
                T = To;
                Vx = Vxo;
                Vy = Vyo;
            }
            if (ti + delTime * 1000 > date.getTime()) {
                try {
                    Thread.sleep((long) (ti + delTime * 1000 - date.getTime())); // небольшая временная остановка
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            workFrame.paint(X, Y);
        }
    }
    // остановка процесса модуляции
    public void noPaint(){
        running = false;
        mass = -1;
        Agr = -1;
        F = -1;
        Fgr = -1;
        T = 1000;
        A = -1;
        Vgr = -1;
        V = -1;
        X = 0;
        Y = 0;
    }
    // перерасчёт скорости
    private double permutationCoordinatesY() {
        if (A < 0){
            return Vy;
        }
        double v = Vy;
        Vy += Ay * T;
        return v;
    }
    private double permutationCoordinatesX() {
        if (A < 0){
            return Vx;
        }
        double v = Vx;
        Vx += Ax * T;
        return v;
    }
}