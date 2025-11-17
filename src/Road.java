public class Road {
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

    public boolean isConnecting(Building a, Building b) {
        return (a.equals(this.a) && b.equals(this.b)) || (a.equals(this.b) && b.equals(this.a));
    }

    public Building roadTo() {
        return b;
    }

    public void drive() {
        carCount++;
    }
}
