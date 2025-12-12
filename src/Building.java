import java.util.ArrayList;

import edu.macalester.graphics.Point;
import edu.macalester.graphics.Rectangle;

public class Building extends Node<Rectangle> {
    public final static Point SIZE = new Point(50, 50);

    private int id;
    private Point location;
    private BuildingType type;
    private ArrayList<Road> roads;
    private Building previous;
    private Road prevRoad;

    public Building(int id, Point location, BuildingType type) {
        this.id = id;
        this.location = location;
        this.type = type;
        this.roads = new ArrayList<>();
    }

    public Rectangle draw() {
        Rectangle rect = new Rectangle(location, SIZE);
        rect.setFilled(true);
        rect.setFillColor(type == BuildingType.RESIDENTIAL ? Palette.BLUE : Palette.RED);
        rect.setStrokeColor(rect.getFillColor());
        rect.setStrokeWidth(2);
        this.setGraphicsObject(rect);
        return rect;
    }

    public BuildingType getType() {
        return type;
    }

    public Point getLocation() {
        return location;
    }

    public ArrayList<Road> getRoads() {
        return roads;
    }

    /**
     * Get the road connecting this building and another.
     * Checks for directional connections only, meaning one-way roads to this building from the other will not be found.
     * @param other other building
     * @return connecting road or null
     */
    public Road getRoadBetween(Building other) {
        for (Road road : this.getRoads()) {
            if (road.isConnecting(this, other)) {
                return road;
            }
        }
        return null;
    }

    
    /**
     * Get the road connecting this building and another.
     * 
     * @param other other building
     * @param checkBothDirections Check both directions, including one-way directional edges.
     * @return connecting road or null
     */
    public Road getRoadBetween(Building other, boolean checkBothDirections) {
        for (Road road : this.getRoads()) {
            if (road.isConnecting(this, other)) {
                return road;
            }
        }
        if (checkBothDirections) {
            return other.getRoadBetween(this);
        }
        return null;
    }

    public void addRoad(Road r) {
        this.roads.add(r);
    }

    public void removeRoad(Road r) {
        this.roads.remove(r);
    }

    /**
     * Check if there is an edge (road connection) to another building.
     * @param other
     */
    public boolean isAdjacentTo(Building other) {
        return this.getRoadBetween(other) != null;
    }

    public int getId() {
        return id;
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