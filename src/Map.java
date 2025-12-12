import java.util.ArrayList;

import edu.macalester.graphics.CanvasWindow;
import edu.macalester.graphics.Point;

public class Map {
    public static final Map BASIC_LAYOUT = new Map("Basic", new Building[] {
        new Building(0, new Point(100, 100), BuildingType.RESIDENTIAL), new Building(1, new Point(200, 100), BuildingType.RESIDENTIAL),
        new Building(2, new Point(100, 200), BuildingType.RESIDENTIAL), new Building(3, new Point(500, 500), BuildingType.COMMERCIAL)
    });

    /**
     * Generate a random map layout in a grid formation.
     * @param canvas
     * @param padding Padding around grid from the edge of the CanvasWindow.
     * @param residentialness Fraction of generated buildings that are residential, 0-100.
     * @param emptiness Fraction of grid locations which are empty, 0-100.
     * @return
     */
    public static Map generateRandomGridLayout(CanvasWindow canvas, double padding, int residentialness, int emptiness) {
        double width = canvas.getWidth() - (padding * 2);
        double height = canvas.getHeight() - (padding * 2);
        double size = Building.SIZE.getX();
        int xFit = (int)((width / size) / 2);
        double xGap = (width - (size * xFit)) / (xFit - 1);
        int yFit = (int)((height / size) / 2);
        double yGap = (height - (size * yFit)) / (yFit - 1);

        int maxBuildingCount = (xFit * yFit);
        ArrayList<Building> buildings = new ArrayList<>(maxBuildingCount);

        int idx = 0;
        for (int row = 0; row < yFit; row++) {
            for (int item = 0; item < xFit; item++) {
                if (Util.randomInt(0, 100) > emptiness) {
                    BuildingType type = Util.randomInt(0, 100) < residentialness ? BuildingType.RESIDENTIAL : BuildingType.COMMERCIAL;
                    Point location = new Point(padding + (item * (size + xGap)), padding + (row * (size + yGap)));
                    buildings.add(new Building(idx, location, type));
                    idx++;
                }
            }
        }

        Building[] ret = buildings.toArray(new Building[buildings.size()]);

        return new Map("Random", ret);
    }

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
