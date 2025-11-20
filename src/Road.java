import edu.macalester.graphics.GraphicsObject;

public class Road {
    private RoadType type;
    private Building a;
    private Building b;
    private GraphicsObject visual;
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

    public GraphicsObject getVisual() {
        return visual;
    }

    public void setVisual(GraphicsObject visual) {
        this.visual = visual;
    }

    public boolean isConnecting(Building a, Building b) {
        return (a.equals(this.a) && b.equals(this.b)) || (a.equals(this.b) && b.equals(this.a));
    }

    public Building roadTo() {
        return b;
    }

    public void drive() {
        carCount++;
    }

    public int getRoadCost() {
        if (this.type == RoadType.HIGHWAY) {
            return 1;
        }
        return 2;
    }
}
