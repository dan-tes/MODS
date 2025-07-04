package Work;

public class Vector {
    int x1, y1, x2, y2;

    Vector(int x1, int y1, int x2, int y2) {
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
            // Вектор нулевой длины — нормализовать нельзя
            return;
        }

        dx /= length;
        dy /= length;

        // Заменяем конец вектора на нормализованную точку
        x2 = x1 + (int)(dx * Settings.vector_size); // умножаем, чтобы сохранить масштаб (иначе будет очень короткий вектор)
        y2 = y1 + (int)(dy * Settings.vector_size);
    }

}
