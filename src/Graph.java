import java.util.ArrayList;

public class Graph {
    private ArrayList<Building> adj;

    public Graph() {
        adj = new ArrayList<>();
    }

    public Building getBuilding(int id) {
        return this.adj.get(id);
    }

    public ArrayList<Building> getBuildings() {
        return adj;
    }

    public Building addBuilding(BuildingType type) {
        int id = this.adj.size();
        this.adj.add(new Building(id, type));
        return this.adj.get(id);
    }

    public Road addRoad(Building a, Building b, RoadType type) {
        Road r = new Road(a, b, type);
        this.adj.get(a.getId()).addRoad(r);
        if (type != RoadType.ONE_WAY) {
            this.adj.get(b.getId()).addRoad(r);
        }
        return r;
    }

    public static void main(String[] args) {
        Graph g = new Graph();
        Building home = g.addBuilding(BuildingType.RESIDENTIAL);
        Building work = g.addBuilding(BuildingType.COMMERCIAL);
        Building shop = g.addBuilding(BuildingType.COMMERCIAL);
        Car car = new Car(home, work);
        car.pathToDestination();
        g.addRoad(home, work, RoadType.HIGHWAY);
        g.addRoad(home, shop, RoadType.TWO_WAY);
        g.addRoad(work, shop, RoadType.TWO_WAY);
    }
}
