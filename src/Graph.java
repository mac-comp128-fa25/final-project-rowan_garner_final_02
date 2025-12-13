import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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

    /**
     * Initialize a new Build at a point with a type, add an automatically determined ID, and add to the internal adjaceny list.
     * @param location
     * @param type
     * @return
     */
    public Building addBuilding(Point location, BuildingType type) {
        int id = this.adj.size();
        this.adj.add(new Building(id, location, type));
        return this.adj.get(id);
    }

    /**
     * Add an existing Building to the adjacency list.
     * @param building
     * @return
     */
    public Building addBuilding(Building building) {
        this.adj.add(building);
        return building;
    }

    /**
     * Initialize a new Road of a specified type between specified buildings and add to (each) building's internal connections/roads.
     * @param a
     * @param b
     * @param type
     * @return
     */
    public Road addRoad(Building a, Building b, RoadType type) {
        Road r = new Road(a, b, type);
        return addRoad(r);
    }

    /**
     * Add a Road to it's connecting building's internal roads.
     * @param road
     * @return
     */
    public Road addRoad(Road road) {
        this.adj.get(road.getEnd().getId()).addRoad(road);
        if (road.getType() != RoadType.ONE_WAY) {
            this.adj.get(road.getStart().getId()).addRoad(road);
        }
        return road;
    }

    public List<Road> getRoads() {
        Set<Road> roadSet = new HashSet<>(); // avoid duplicates
        for (Building b : adj) {
            roadSet.addAll(b.getRoads());
        }
        return new ArrayList<>(roadSet);
    }
}
