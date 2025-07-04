package Graphics;

import Physics.MaterialVector;
import Physics.MechanicalParameters;
import Physics.Point;

import javax.swing.*;
import java.awt.*;
import java.util.*;
import java.util.List;

import static Physics.MetricNames.*;



class WorkFrame extends JFrame {
    private static final int POINT_RADIUS = 5;

    private final JPanel controlPanel;
    private final JPanel drawPanel;
    private final JSpinner[][] spinners;
    private final String[] selectedMetrics;
    private int selectedCount = 0;
    private boolean isModelRunning = false;
    Vector<String> metricOptions = new Vector<>(List.of(VELOCITY, ACCELERATION, TIME, X, Y));
    Set<String> vectorMetrics = Set.of(VELOCITY, ACCELERATION);
    private int pointX = -1;
    private int pointY = -1;

    private final WorkMain workMain;

    public WorkFrame(WorkMain workMain) {
        this.workMain = workMain;
        this.setLayout(new BorderLayout());

        spinners = new JSpinner[metricOptions.size()][];
        selectedMetrics = new String[metricOptions.size()];

        controlPanel = createControlPanel();
        drawPanel = createDrawPanel();

        this.add(controlPanel, BorderLayout.WEST);
        this.add(drawPanel, BorderLayout.CENTER);

        setSize(Settings.Width, Settings.Height);
        setTitle("MODS");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setVisible(true);
    }

    private JPanel createControlPanel() {
        JPanel panel = new JPanel(new FlowLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                g.setColor(Color.WHITE);
                g.fillRect(0, 0, 1000, 1000);
            }
        };
        panel.setPreferredSize(new Dimension(220, Settings.Height));

        JButton startButton = new JButton("Начать моделирование");
        JButton pauseButton = new JButton("Пауза");

        startButton.setPreferredSize(new Dimension(180, 25));
        startButton.addActionListener(e -> handleModelStartOrStop(startButton, pauseButton));

        pauseButton.setPreferredSize(new Dimension(180, 25));
        pauseButton.addActionListener(e -> pauseControlPanel(pauseButton));
        pauseButton.setEnabled(false);

        panel.add(startButton);
        panel.add(pauseButton);
        for (String metric : metricOptions) {
            boolean isVector = vectorMetrics.contains(metric);
            spinners[selectedCount] = addInputField(panel, metric, isVector);
            selectedMetrics[selectedCount++] = metric;
        }
        return panel;
    }

    private void pauseControlPanel(JButton pauseButton) {
        if (pauseButton.getText().equals("Пауза")) {
            pauseButton.setText("Продолжить");
            toggleInputs(true);
            workMain.pause();
        } else {
            pauseButton.setText("Пауза");
            toggleInputs(false);
            handler_spinner();
            workMain.startModeling();
        }
    }

    private JPanel createDrawPanel() {
        return new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);

                g.setColor(Color.BLACK);
                g.fillRect(0, 0, 5, 2000);

                g.setColor(Color.WHITE);
                g.fillRect(5, 0, Settings.Width - 200, Settings.Height);

                g.setColor(Color.BLACK);
                g.fillRect(15, 5, 5, Settings.Height - 55);
                g.fillRect(15, Settings.Height - 50, Settings.Width - 400, 5);

                if (isModelRunning) {
                    g.setColor(Color.RED);
                    g.fillOval(pointX, pointY, POINT_RADIUS, POINT_RADIUS);

                    if (Settings.show_vector) {
                        drawVector(g, workMain.getVx(), workMain.getVy(), Color.BLUE);
                        drawVector(g, workMain.getAx(), workMain.getAy(), Color.ORANGE);
                    }
                }
            }
        };
    }

    private void drawVector(Graphics g, double vx, double vy, Color color) {
        MaterialVector vector = new MaterialVector(
                pointX + POINT_RADIUS / 2,
                pointY + POINT_RADIUS / 2,
                pointX + (int) vx + POINT_RADIUS / 2,
                pointY - (int) vy + POINT_RADIUS / 2
        );
        vector.normalize();
        g.setColor(color);
        g.drawLine(vector.x1, vector.y1, vector.x2, vector.y2);
    }

    private void handleModelStartOrStop(JButton startButton, JButton pauseButton) {
        if (!isModelRunning) {
            handler_spinner();
            if (!workMain.startModeling()) return;

            pauseButton.setEnabled(true);
            startButton.setText("Закрыть модель");
            toggleInputs(false);
            isModelRunning = true;
        } else {
            workMain.reset();
            toggleInputs(true);
            startButton.setText("Начать моделирование");
            pointX = pointY = -1;
            for (JSpinner[] spinnerRow : spinners) {
                if (spinnerRow != null) {
                    for (JSpinner spinner : spinnerRow) {
                        SpinnerNumberModelDefault model = (SpinnerNumberModelDefault) spinner.getModel();
                        spinner.setValue(model.getDefault_val());
                    }
                }
            }
            pauseButton.setEnabled(false);
            pauseButton.setText("Пауза");
            revalidate();
            repaint();
            isModelRunning = false;
        }
    }

    private void handler_spinner() {
        for (int i = 0; i < selectedCount; i++) {
            String type = selectedMetrics[i];
            switch (type) {
                case ACCELERATION -> workMain.setA(getVal(i, 0), getVal(i, 1));
                case VELOCITY -> workMain.setV(getVal(i, 0), getVal(i, 1));
                case TIME -> workMain.setT(getVal(i, 0));
                case X -> workMain.setX(getVal(i, 0));
                case Y -> workMain.setY(getVal(i, 0));
            }
        }
    }

    private void toggleInputs(boolean enabled) {
        for (JSpinner[] row : spinners) {
            if (row != null) {
                for (JSpinner spinner : row) {
                    spinner.setEnabled(enabled);
                }
            }
        }
    }

    private int getVal(int idx, int sub) {
        return (int) spinners[idx][sub].getValue();
    }

    private void setVal(int idx, int sub, int val) {
        spinners[idx][sub].setValue(val);
    }

    private JSpinner[] addInputField(JPanel panel, String name, boolean isVector) {
        int default_val = switch (name) {
            case TIME -> 1000;
            case VELOCITY -> Settings.testing ? 30 : 0;
            default -> 0;
        };

        JPanel panel_input = new JPanel();
        panel_input.setBackground(Color.WHITE);
        panel_input.setPreferredSize(new Dimension(220, 40));
        panel_input.add(new JLabel(name + "-"));

        if (!isVector) {
            JSpinner spinner = createSpinner(default_val);
            panel_input.add(spinner);
            panel.add(panel_input);
            revalidate();
            return new JSpinner[]{spinner};
        } else {
            JSpinner mag = createSpinner(default_val);
            JSpinner angle = createAngleSpinner();
            panel_input.add(mag);
            panel_input.add(new JLabel("Гр-"));
            panel_input.add(angle);
            panel.add(panel_input);
            revalidate();
            return new JSpinner[]{mag, angle};
        }
    }

    private JSpinner createSpinner(int default_val) {
        SpinnerNumberModel model = new SpinnerNumberModelDefault(default_val, 0, 9999, 1, default_val);
        JSpinner spinner = new JSpinner(model);
        spinner.setPreferredSize(new Dimension(35, 25));
        return spinner;
    }

    private JSpinner createAngleSpinner() {
        SpinnerNumberModel model = new SpinnerNumberModelDefault(0, 0, 360, 1, 0);
        JSpinner spinner = new JSpinner(model);
        spinner.setPreferredSize(new Dimension(35, 25));
        return spinner;
    }

    public void paint(MechanicalParameters parameters) {
        if (!isModelRunning) return;
        Physics.Point point = Point.changing_coordinate_system(parameters.getX(), parameters.getY());
        this.pointX = (int) point.getX();
        this.pointY = (int) point.getY();

        for (int i = 0; i < selectedCount; i++) {
            String type = selectedMetrics[i];
            switch (type) {
                case VELOCITY -> {
                    setVal(i, 0, parameters.getV());
                    setVal(i, 1, parameters.getAgree_v());
                }
                case X -> setVal(i, 0, parameters.getX());
                case Y -> setVal(i, 0, parameters.getY());
            }
        }
        repaint();
    }
}
