public class Road {
    private RoadType type;
    private Building a;
    private Building b;

    public Road(Building a, Building b, RoadType type) {
        this.a = a;
        this.b = b;
        this.type = type;
    }

    public RoadType getType() {
        return type;
    }

    public boolean isConnecting(Building a, Building b) {
        return (a.equals(this.a) && b.equals(this.b)) || (a.equals(this.b) && b.equals(this.a));
    }
}
