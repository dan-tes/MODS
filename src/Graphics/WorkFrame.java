package Graphics;

import Physics.MaterialVector;
import Physics.MechanicalParameters;
import Physics.MetricNames;
import settings.Settings;

import javax.swing.*;
import java.awt.*;
import java.util.*;
import java.util.List;

import static java.awt.Point.*;

class WorkFrame extends JFrame {
    private static final int POINT_RADIUS = 5;

    private final JPanel controlPanel;
    private final JPanel drawPanel;
    private final JSpinner[][] spinners;
    private final String[] selectedMetrics;
    private int selectedCount = 0;
    private boolean isModelRunning = false;
    Vector<String> metricOptions = new Vector<>(List.of(
            MetricNames.VELOCITY,
            MetricNames.ACCELERATION,
            MetricNames.TIME,
            MetricNames.X,
            MetricNames.Y
    ));
    Set<String> vectorMetrics = Set.of(
            MetricNames.VELOCITY,
            MetricNames.ACCELERATION
    );
    private int pointX = -1;
    private int pointY = -1;

    private final int virtualWidth = 1000;
    private final int virtualHeight = 1000;

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

    private int scaleX(double x) {
        return (int) ((x / virtualWidth) * drawPanel.getWidth());
    }

    private int scaleY(double y) {
        return (int) (drawPanel.getHeight() - (y / virtualHeight) * drawPanel.getHeight());
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
        panel.setPreferredSize(new Dimension(300, Settings.Height));

        JButton startButton = new JButton("Начать моделирование");
        JButton pauseButton = new JButton("Пауза");

        startButton.setPreferredSize(new Dimension(180, 25));
        startButton.addActionListener(e -> handleModelStartOrStop(startButton, metricOptions, pauseButton));

        pauseButton.setPreferredSize(new Dimension(180, 25));
        pauseButton.addActionListener(e -> pauseControlPanel(panel, metricOptions, pauseButton));
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

    private void pauseControlPanel(JPanel panel, Vector<String> metricOptions, JButton pauseButton) {
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

                g.setColor(Color.WHITE);
                g.fillRect(0, 0, getWidth(), getHeight());

                g.setColor(Color.BLACK);
                g.fillRect(0, 0, 2, getHeight());
                g.fillRect(scaleX(30), 10, 2, getHeight());
                g.fillRect(10, scaleY(30), getWidth(), 2);

                // Draw grid
//                g.setColor(Color.LIGHT_GRAY);
//                for (int i = 0; i <= virtualWidth; i += 100) {
//                    int x = scaleX(i);
//                    g.drawLine(x, scaleY(900), x, 400);
//                }
//                for (int i = 0; i <= virtualHeight; i += 100) {
//                    int y = scaleY(i);
//                    g.drawLine(0, y, getWidth(), y);
//                }

                if (isModelRunning) {
                    g.setColor(Color.RED);
                    g.fillOval(pointX, pointY, POINT_RADIUS, POINT_RADIUS);

                    if (Settings.show_vector) {
                        drawVector(g, workMain.getVx(), workMain.getVy(), Color.BLUE);
                        drawVector(g, workMain.getAx(), workMain.getAy(), Color.GREEN);
                    }
                }
            }
        };
    }

    private void drawVector(Graphics g, double vx, double vy, Color color) {
        MaterialVector vector = new MaterialVector(
                pointX + POINT_RADIUS / 2,
                pointY + POINT_RADIUS / 2,
                pointX + vx + POINT_RADIUS / 2,
                pointY - vy + POINT_RADIUS / 2
        );
        vector.normalize();
        g.setColor(color);
        g.drawLine(vector.getX1(), vector.getY1(), vector.getX2(), vector.getY2());
    }

    private void handleModelStartOrStop(JButton startButton, Vector<String> originalOptions, JButton pauseButton) {
        if (!isModelRunning) {
            handler_spinner();

            if (!workMain.startModeling()) {
                return;
            }
            pauseButton.setEnabled(true);
            startButton.setText("Закрыть модель");
            toggleInputs(false);
            isModelRunning = true;
        } else {
            workMain.reset();
            toggleInputs(true);

            startButton.setText("Начать моделирование");
            pointX = pointY = -1;
            for (JSpinner[] spinner : spinners) {
                if (spinner == null) continue;
                for (JSpinner jSpinner : spinner) {
                    SpinnerNumberModelDefault model = (SpinnerNumberModelDefault) jSpinner.getModel();
                    jSpinner.setValue(model.getDefault_val());
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
                case MetricNames.ACCELERATION -> workMain.setA(getVal(i, 0), getVal(i, 1));
                case MetricNames.VELOCITY -> workMain.setV(getVal(i, 0), getVal(i, 1));
                case MetricNames.TIME -> workMain.setT(getVal(i, 0));
                case MetricNames.X -> workMain.setX(getVal(i, 0));
                case MetricNames.Y -> workMain.setY(getVal(i, 0));
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
        int default_val = 0;
        if (Objects.equals(name, MetricNames.TIME)) {
            default_val = 1000;
        } else if (Objects.equals(name, MetricNames.VELOCITY) && Settings.testing) {
            default_val = 30;
        }
        JPanel panel_input = new JPanel();
        panel_input.setBackground(Color.WHITE);
        panel_input.setPreferredSize(new Dimension(300, 40));
        JLabel label = new JLabel(name + "-");
        panel_input.add(label);

        if (!isVector) {
            JSpinner spinner = createSpinner(default_val);
            panel_input.add(spinner);
            panel.add(panel_input);

            revalidate();
            return new JSpinner[]{spinner};
        } else {
            JSpinner mag = createSpinner(default_val);
            JSpinner angle = createAngleSpinner();

            JLabel angleLabel = new JLabel("Гр-");
            panel_input.add(mag);
            panel_input.add(angleLabel);
            panel_input.add(angle);
            panel.add(panel_input);

            revalidate();
            return new JSpinner[]{mag, angle};
        }
    }

    private JSpinner createSpinner(int default_val) {
        SpinnerNumberModel model = new SpinnerNumberModelDefault(default_val, 0, 9999, 1, default_val);
        JSpinner spinner = new JSpinner(model);
        spinner.setPreferredSize(new Dimension(50, 25));
        return spinner;
    }

    private JSpinner createAngleSpinner() {
        SpinnerNumberModel model = new SpinnerNumberModelDefault(0, 0, 360, 1, 0);
        JSpinner spinner = new JSpinner(model);
        spinner.setPreferredSize(new Dimension(50, 25));
        return spinner;
    }

    public void paint(MechanicalParameters parameters) {
        if (!isModelRunning) return;
        Physics.Point point = Physics.Point.changing_coordinate_system(parameters.getX(), parameters.getY());
        this.pointX = (int) point.getX();
        this.pointY = (int) point.getY();
//        this.pointX = scaleX(parameters.getX());
//        this.pointY = scaleY(parameters.getY());

        for (int i = 0; i < selectedCount; i++) {
            String type = selectedMetrics[i];
            switch (type) {
                case MetricNames.VELOCITY -> {
                    setVal(i, 0, parameters.getV());
                    setVal(i, 1, parameters.getAgree_v());
                }
                case MetricNames.X -> setVal(i, 0, parameters.getX());
                case MetricNames.Y -> setVal(i, 0, parameters.getY());
            }
        }
        repaint();
    }
}
