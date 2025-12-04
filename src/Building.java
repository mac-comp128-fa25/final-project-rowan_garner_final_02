import java.util.ArrayList;

import edu.macalester.graphics.GraphicsObject;

public class Building {
    private int id;
    private BuildingType type;
    private ArrayList<Road> roads;
    private GraphicsObject visual;
    private Building previous;
    private Road prevRoad;

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

    public Road getRoadBetween(Building b) {
        for (Road road : this.getRoads()) {
             if (road.isConnecting(this, b)) {
                return road;
            }
        }
        return null;
    }

    public void addRoad(Road r) {
        this.roads.add(r);
    }

    public boolean isAdjacentTo(Building other) {
        return this.getRoadBetween(other) != null;
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


    public void setPrevRoad(Road r) {
        this.prevRoad = r;
    }

    public Road getPrevRoad() {
        return prevRoad;
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