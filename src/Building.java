import java.util.ArrayList;

public class Building {
    private int id;
    private BuildingType type;
    private ArrayList<Road> roads;

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