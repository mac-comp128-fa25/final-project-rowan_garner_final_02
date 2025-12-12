import edu.macalester.graphics.GraphicsGroup;
import edu.macalester.graphics.GraphicsObject;
import edu.macalester.graphics.Line;
import edu.macalester.graphics.Point;

public class Road extends Node<GraphicsObject> {
    private RoadType type;
    private Building a;
    private Building b;
    private int carCount;

    public Road(Building a, Building b, RoadType type) {
        this.a = a;
        this.b = b;
        this.type = type;
        carCount = 0;
    }

    public RoadType getType() {
        return type;
    }

    public void setType(RoadType type) {
        this.type = type;
    }

    public GraphicsGroup draw() {
        Point c1 = a.getGraphicsObject().getCenter();
        Point c2 = b.getGraphicsObject().getCenter();

        GraphicsGroup visual = new GraphicsGroup();
        Line line = new Line(c1, c2);
        line.setStrokeColor(Palette.BACKGROUND_WHITE);
        visual.add(line);
        switch (type) {
            case RoadType.ONE_WAY: {
                line.setStrokeWidth(4);
                break;
            }
            case RoadType.TWO_WAY: {
                line.setStrokeWidth(8);
                Line divider = new Line(c1, c2);
                divider.setStrokeColor(Palette.HIGHLIGHT_YELLOW);
                divider.setStrokeWidth(1);
                visual.add(divider);
                break;
            }
            case RoadType.HIGHWAY: {
                line.setStrokeWidth(16);
                Line divider = new Line(c1, c2);
                divider.setStrokeColor(Palette.HIGHLIGHT_YELLOW);
                divider.setStrokeWidth(1);
                visual.add(divider);
                break;
            }
        }
        this.setGraphicsObject(visual);
        return visual;
    }

    public boolean isConnecting(Building a, Building b) {
        return (a.equals(this.a) && b.equals(this.b)) || (a.equals(this.b) && b.equals(this.a));
    }

    public Building roadEnd() {
        return b;
    }

    public Building roadStart() {
        return a;
    }

    public void drive() {
        carCount++;
        // wait();
        carCount--;
    }

    public int getRoadCost() {
        if (this.type == RoadType.HIGHWAY) {
            return 1;
        }
        return 2;
    }
}
