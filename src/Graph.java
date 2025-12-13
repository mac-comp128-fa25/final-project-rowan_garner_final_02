import java.util.ArrayList;

import edu.macalester.graphics.Point;

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

    public Building addBuilding(Point location, BuildingType type) {
        int id = this.adj.size();
        this.adj.add(new Building(id, location, type));
        return this.adj.get(id);
    }

    public Building addBuilding(Building building) {
        this.adj.add(building);
        return building;
    }

    public Road addRoad(Building a, Building b, RoadType type) {
        Road r = new Road(a, b, type);
        return addRoad(r);
    }

    public Road addRoad(Road road) {
        this.adj.get(road.getEnd().getId()).addRoad(road);
        if (road.getType() != RoadType.ONE_WAY) {
            this.adj.get(road.getStart().getId()).addRoad(road);
        }
        return road;
    }
}
