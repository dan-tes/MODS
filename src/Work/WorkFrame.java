package Work;

import javax.swing.*;
import java.awt.*;
import java.util.Objects;
import java.util.Scanner;
import java.util.Vector;

class WorkFrame extends JFrame {
    JPanel setPanel;

    JPanel getPanel;
    //    int Height, Setting.Width;
    JSpinner[][] spinners;
    int spin = 0;
    String[] strut;
    private int X = -1, Y = -1; // координаты точки
    private static final int R = 5; // радиус точки
    WorkMain workMain;
    boolean flagBig = true; // состояние программы

    WorkFrame(WorkMain workMain) {
        this.workMain = workMain;
        setLayout(new BorderLayout());
        setPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                g.setColor(Color.WHITE);
                g.fillRect(0, 0, 1000, 1000);

            }
        };
        // все векторные величины
        String vector = """
                Сила Н
                Скорость м/с
                Ускорение м/с2
                """;
        setPanel.setLayout(new FlowLayout());
        // все величины
        Scanner scanner = new Scanner("""
                Ничего не выбрано
                Сила Н
                Масса кг
                Скорость м/с
                Ускорение м/с2
                Время мс
                X м
                Y м
                """);
        Vector<String> metrics = new Vector<>();
        while (scanner.hasNextLine()) {
            metrics.add(scanner.nextLine());
        }
        int n = metrics.toArray().length;
        spinners = new JSpinner[n][];
        strut = new String[n];
        JComboBox<String> jComboBox = new JComboBox<>(metrics);
        jComboBox.addActionListener(e -> {
            if (jComboBox.getSelectedIndex() != 0) {
                String s = jComboBox.getItemAt(jComboBox.getSelectedIndex());
                jComboBox.removeItem(s);
                if (!vector.contains(s))
                    spinners[spin] = newElement(s, setPanel, true);
                else
                    spinners[spin] = newElement(s, setPanel, false);
                jComboBox.setSelectedIndex(0);
                strut[spin++] = s;
            }
        });
        JButton button_again = new JButton("Начать заново");
        setPanel.add(jComboBox);
        JButton button = new JButton("Начать моделирование");
        button.addActionListener(e -> {
            if (flagBig) {
                for (int i = 0; i < spin; i++) {
                    // сбор данных и их отправка в workmain
                    switch (strut[i]) {
                        case "Ускорение м/с2" ->
                                workMain.setA((int) spinners[i][0].getValue(), (int) spinners[i][1].getValue());
                        case "Сила Н" ->
                                workMain.setF((int) spinners[i][0].getValue(), (int) spinners[i][1].getValue());
                        case "Масса кг" -> workMain.setMass((int) spinners[i][0].getValue());
                        case "Скорость м/с" ->
                                workMain.setV((int) spinners[i][0].getValue(), (int) spinners[i][1].getValue());
                        case "Время мс" -> workMain.setT((int) spinners[i][0].getValue());
                        case "X м" -> workMain.setX((int) spinners[i][0].getValue());
                        case "Y м" -> workMain.setY((int) spinners[i][0].getValue());
                    }
                }
                if (!workMain.startModeling()) {
                    setPanel.removeAll();
                    es:
                    for (String s : metrics) {
                        for (int j = 0; j <= jComboBox.getMaximumRowCount(); j++) {
                            if (Objects.equals(s, jComboBox.getItemAt(j)))
                                continue es;
                        }
                        jComboBox.addItem(s);
                    }
                    setPanel.add(jComboBox);
                    setPanel.add(button);
                    setPanel.add(button_again);
                    repaint();
                } else {
                    button.setText("Закрыть модель");
                    flagBig = false;
                    restart(button_again, jComboBox, false);
                }
            } else {
                workMain.noPaint();
                restart(button_again, jComboBox, true);
                button.setText("Начать моделирование");
                X = -1;
                Y = -1;
                repaint();
                flagBig = true;
            }
        });
        button.setPreferredSize(new Dimension(180, 25));
        setPanel.add(button);
        button_again.setPreferredSize(new Dimension(180, 25));
        button_again.addActionListener(e -> {
            setPanel.removeAll();
            es:
            for (String s : metrics) {
                for (int j = 0; j <= jComboBox.getMaximumRowCount(); j++) {
                    if (Objects.equals(s, jComboBox.getItemAt(j)))
                        continue es;
                }
                jComboBox.addItem(s);
            }
            setPanel.add(jComboBox);
            setPanel.add(button);
            setPanel.add(button_again);
            repaint();
        });
        setPanel.add(button_again);
        setPanel.setPreferredSize(new Dimension(200, Settings.Height));
        add(setPanel, BorderLayout.WEST);
        getPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g); // прорисовка компонентов
                g.setColor(Color.BLACK);
                g.fillRect(0, 0, 5, 2000);
                g.setColor(Color.WHITE);
                g.fillRect(5, 0, Settings.Width - 200, Settings.Height);
                g.setColor(Color.BLACK);
                g.fillRect(15, 5, 5, Settings.Height - 55);
                g.fillRect(15, Settings.Height - 50, Settings.Width - 400, 5);
                g.setColor(Color.RED);
                if (!flagBig) {
                    g.fillOval(X, Y, R, R);
                    Point vector_v = new Point(X + (int) workMain.getVx() + R / 2, Y - (int) workMain.getVy() + R / 2);
                    Point vector_a = new Point(X + (int) workMain.getAx() + R / 2, Y - (int) workMain.getAy() + R / 2);

                    g.setColor(Color.BLUE);
                    g.drawLine(X + R / 2, Y + R / 2, (int) vector_v.getX(), (int) vector_v.getY());
                    g.drawLine(X + R / 2, Y + R / 2, (int) vector_a.getX(), (int) vector_a.getY());
                }
//                g.fillPolygon(new int[] {X, workMain.g}, new int[] {Y}, 2);
            }
        };
        getPanel.setPreferredSize(new Dimension(Settings.Width - Settings.Width * 2 / 7, Settings.Height));
        add(getPanel, BorderLayout.CENTER);
        setSize(new Dimension(Settings.Width, Settings.Height));
        setTitle("MODS");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setVisible(true);
    }

    // рестарт списка настроек точки
    private void restart(JButton button_again, JComponent comboBox, boolean b) {
        for (JSpinner[] spinner : spinners)
            if (spinner != null)
                for (JSpinner jSpinner : spinner) jSpinner.setEnabled(b);
        button_again.setEnabled(b);
        comboBox.setEnabled(b);
    }

    // настройка сбора информации
    private JSpinner[] newElement(String name, JPanel panel, boolean check) {
        JLabel label = new JLabel();
        JSpinner spinner = new JSpinner();
        label.setText(name + "-");
        if (check) {
            label.setPreferredSize(new Dimension(100, 25));
            spinner.setPreferredSize(new Dimension(70, 25));
            spinner.addChangeListener(e -> {
                if ((int) spinner.getValue() < 0) {
                    spinner.setValue(0);
                }
            });
            panel.add(label);
            panel.add(spinner);
            revalidate();
            return new JSpinner[]{spinner};
        } else {
            label.setPreferredSize(new Dimension(90, 25));
            spinner.setPreferredSize(new Dimension(35, 25));
            JLabel labelGr = new JLabel("Гр-");
            JSpinner spinnerGr = new JSpinner();
            spinnerGr.addChangeListener(e -> {
                int n = (int) spinnerGr.getValue();
                if (n < 0 || n > 360) { // границы
                    spinnerGr.setValue(0);
                }
            });
            spinner.addChangeListener(e -> {
                if ((int) spinner.getValue() < 0) { // границы
                    spinner.setValue(0);
                }
            });
            spinnerGr.setPreferredSize(new Dimension(35, 25));
            panel.add(label);
            panel.add(spinner);
            panel.add(labelGr);
            panel.add(spinnerGr);
            revalidate();
            return new JSpinner[]{spinner, spinnerGr};
        }
    }

    // замена координат и перерисовка точки
    public void paint(double x, double y) {
        Point ransform_point = Point.СhangingСoordinateSystem(x, y);
        X = (int) ransform_point.getX();
        Y = (int) ransform_point.getY();
        repaint();
    }
}