package Physics;

public class MechanicalParameters {
    int x, y, v, agree_v;

    public MechanicalParameters(int x, int y, int v, int agree_v) {
        this.x = x;
        this.y = y;
        this.v = v;
        this.agree_v = agree_v;
    }

    public int getV() {
        return v;
    }

    public int getX() {
        return x;
    }

    public int getAgree_v() {
        return agree_v;
    }

    public int getY() {
        return y;
    }
}
