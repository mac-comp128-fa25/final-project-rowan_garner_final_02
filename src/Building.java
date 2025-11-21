import java.util.ArrayList;

import edu.macalester.graphics.GraphicsObject;

public class Building {
    private int id;
    private BuildingType type;
    private ArrayList<Road> roads;
    private GraphicsObject visual;
    private Building previous; // for using this as a node

    public Building(int id, BuildingType type) {
        this.id = id;
        this.type = type;
        this.roads = new ArrayList<>();
    }

    public BuildingType getType() {
        return type;
    }

    public ArrayList<Road> getRoads() {
        return roads;
    }

    public void addRoad(Road r) {
        this.roads.add(r);
    }

    public boolean isAdjacentTo(Building other) {
        for (Road r : roads) {
            if (r.isConnecting(this, other)) {
                return true;
            }
        }
        return false;
    }

    public int getId() {
        return id;
    }

    public GraphicsObject getVisual() {
        return visual;
    }

    public void setVisual(GraphicsObject visual) {
        this.visual = visual;
    }

    // Functions to use Buildings as Nodes
    public boolean hasPrev() {
        if (this.prev() != null) {
            return true;
        }
        return false;
    }


    public Building prev() {
        return this.previous;
    }


    public void prev(Building building) {
        this.previous = building;
    }


    // For creating a path after Dijkstra's. Some implementation that sets connections using roads would not require this for finding connections
    public Road roadBetween(Building b) {
        for (Road road : this.getRoads()) {
            if (road.roadTo().equals(b)) {
                return road;
            }
        }
        return null;
    }

    @Override
    public boolean equals(Object o) {
        if (o == this)
            return true;
        if (!(o instanceof Building))
            return false;
        Building other = (Building)o;
        return this.id == other.id;
    }
}