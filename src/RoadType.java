public enum RoadType {
    ONE_WAY(25), TWO_WAY(50), HIGHWAY(150);

    public final int cost;

    private RoadType(int cost) {
        this.cost = cost;
    }
}