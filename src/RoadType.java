public enum RoadType {
    ONE_WAY(24), TWO_WAY(25), HIGHWAY(150);

    public final int cost;

    private RoadType(int cost) {
        this.cost = cost;
    }
}