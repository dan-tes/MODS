package Graphics;

import Physics.MechanicalParameters;

public class GraphicsEngine {
    private final WorkFrame frame;

    public GraphicsEngine(WorkMain model) {
        this.frame = new WorkFrame(model);
    }

    public void render(MechanicalParameters parameters) {
        frame.paint(parameters);
    }
}
