package Graphics;

import Physics.MechanicalParameters;
import Physics.WorkMain;

public class GraphicsEngine {
    private final WorkFrame frame;

    public GraphicsEngine(WorkMain model) {
        this.frame = new WorkFrame(model);
    }

    public void render(MechanicalParameters parameters) {
        frame.paint(parameters);
    }
}
