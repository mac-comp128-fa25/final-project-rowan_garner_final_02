import edu.macalester.graphics.Point;

public class Map {
    public static final Map BASIC_LAYOUT = new Map("Basic", new Building[] {
        new Building(0, new Point(100, 100), BuildingType.RESIDENTIAL), new Building(1, new Point(200, 100), BuildingType.RESIDENTIAL),
        new Building(2, new Point(100, 200), BuildingType.RESIDENTIAL), new Building(3, new Point(500, 500), BuildingType.COMMERCIAL)
    });

    private String name;
    private Building[] buildings;

    public Map(String name, Building[] buildings) {
        this.name = name;
        this.buildings = buildings;
    }

    public String getName() {
        return name;
    }

    public Building[] getBuildings() {
        return buildings;
    }
}
