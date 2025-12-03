import java.awt.Color;
import java.util.ArrayList;
import java.util.Iterator;

import edu.macalester.graphics.CanvasWindow;
import edu.macalester.graphics.FontStyle;
import edu.macalester.graphics.GraphicsGroup;
import edu.macalester.graphics.GraphicsObject;
import edu.macalester.graphics.GraphicsText;
import edu.macalester.graphics.Line;
import edu.macalester.graphics.Point;
import edu.macalester.graphics.Rectangle;
import edu.macalester.graphics.events.Key;
import edu.macalester.graphics.events.MouseButtonEvent;
import edu.macalester.graphics.events.MouseMotionEvent;
import edu.macalester.graphics.ui.Button;

public class Simulator {
    private CanvasWindow canvas;
    private Graph graph;

    private GraphicsGroup graphGroup;

    private GraphicsGroup menuGroup;
    private GraphicsGroup buildRoadOptions;

    private Button runButton;
    private GraphicsText budgetBalance;
    private ArrayList<Building> selectedBuildings;

    private int balance = 1000000;

    private static final Color TEXT_BLACK = new Color(243, 246, 247);
    private static final Color BACKGROUND_WHITE = new Color(21, 21, 20);
    private static final Color HIGHLIGHT_YELLOW = new Color(255, 224, 102);
    private static final Color MONEY_GREEN = new Color(22, 152, 115);
    private static final Color BUILDING_BLUE = new Color(36, 123, 160);
    private static final Color SURFACE_GRAY = new Color(219, 224, 230);


    public Simulator() {
        canvas = new CanvasWindow("Traffic Simulator", 800, 600);
        canvas.setBackground(TEXT_BLACK);
        graph = new Graph();

        budgetBalance = new GraphicsText();
        updateBudgetBalance();
        budgetBalance.setFont(FontStyle.PLAIN, 24);
        budgetBalance.setFillColor(MONEY_GREEN);
        canvas.add(budgetBalance, 10, 30);

        runButton = new Button("Run Simluation");
        runButton.onClick(() -> runSimulation());
        canvas.add(runButton, canvas.getWidth() - runButton.getWidth() - 10, 10);


        graphGroup = new GraphicsGroup();
        generateRandomBuildings(10);
        canvas.add(graphGroup);

        // TODO: Add road modification and destruction menus.

        menuGroup = new GraphicsGroup();
        Point menuDimensions = new Point(canvas.getWidth() / 4, canvas.getHeight() / 3);
        Point topLeft = new Point(canvas.getWidth() - canvas.getWidth() / 4, (canvas.getHeight() / 3) * 2);
        Rectangle menuBackground = new Rectangle(topLeft, menuDimensions);
        menuBackground.setFilled(true);
        menuBackground.setFillColor(SURFACE_GRAY);
        menuBackground.setStroked(false);
        menuGroup.add(menuBackground);

        buildRoadOptions = new GraphicsGroup();
        int gap = 5;
        GraphicsText label = new GraphicsText("Build Road");
        label.setFillColor(BACKGROUND_WHITE);
        label.setPosition(topLeft.getX() + gap, topLeft.getY() + label.getHeight() + gap);

        Button oneWay = new Button("One Way");
        oneWay.setPosition(label.getX(), label.getY() + label.getHeight() + gap);
        oneWay.onClick(buildRoadBetweenTwoBuildings(RoadType.ONE_WAY));
        Button twoWay = new Button("Two Way (Regular)");
        twoWay.setPosition(label.getX(), oneWay.getY() + oneWay.getHeight() + gap);
        twoWay.onClick(buildRoadBetweenTwoBuildings(RoadType.TWO_WAY));
        Button highway = new Button("Highway");
        highway.setPosition(label.getX(), twoWay.getY() + twoWay.getHeight() + gap);
        highway.onClick(buildRoadBetweenTwoBuildings(RoadType.HIGHWAY));
        buildRoadOptions.add(label);
        buildRoadOptions.add(oneWay);
        buildRoadOptions.add(twoWay);
        buildRoadOptions.add(highway);

        selectedBuildings = new ArrayList<>();
        PositionEventHandler handler = (event) -> {
            GraphicsObject el = canvas.getElementAt(event.getPosition());
            boolean isHoldingShift = canvas.getKeysPressed().contains(Key.SHIFT);

            if (el == null && !isHoldingShift) {
                // Clicked outside of buildings clears selection.
                Iterator<Building> iter = selectedBuildings.iterator();
                while (iter.hasNext()) {
                    Building building = iter.next();
                    markUnselected(building);
                    iter.remove();
                }
            } else if (el instanceof Rectangle rect && isHoldingShift) {
                // If user is holding shift and this object is a rectangle.
                // Scan buildings...
                for (Building building : graph.getBuildings()) {
                    // Find matching GraphicsObject.
                    if (building.getVisual() == rect) {
                        // Mark as selected.
                        if (selectedBuildings.remove(building)) {
                            markUnselected(building);
                        } else {
                            selectedBuildings.add(building);
                            rect.setStrokeColor(HIGHLIGHT_YELLOW);
                        }
                        break;
                    }
                }
            }

            if (selectedBuildings.size() == 2) {
                if (buildRoadOptions.getParent() == null) {
                    menuGroup.add(buildRoadOptions);
                    canvas.add(menuGroup);
                }
            } else {
                if (buildRoadOptions.getParent() != null) {
                    menuGroup.remove(buildRoadOptions);
                    canvas.remove(menuGroup);
                }
            }
        };

        canvas.onClick(e ->
            handler.handleEvent(new PositionEventAdapter(e))
        );
        canvas.onDrag(e ->
            handler.handleEvent(new PositionEventAdapter(e))
        );
    }

    private Runnable buildRoadBetweenTwoBuildings(RoadType roadType) {
        return () -> {
            assert selectedBuildings.size() == 2;
            Building buildingA = selectedBuildings.get(0);
            Building buildingB = selectedBuildings.get(1);
            Road road = this.graph.addRoad(buildingA, buildingB, roadType);

            Point c1 = buildingA.getVisual().getCenter();
            Point c2 = buildingB.getVisual().getCenter();

            // Point vectorToward = new Point(c1.getX() - c2.getX(), c1.getY() - c2.getY());
            // Point vectorSideways = new Point(-(vectorToward.getX()), vectorToward.getY());
            // Point vectorAlternateSideways = new Point(vectorToward.getX(), -(vectorToward.getY()));
            // Point corner1 = Point.interpolate(vectorSideways, c1, 50.0 / vectorSideways.distance(c1));
            // Point corner2 = Point.interpolate(vectorAlternateSideways, c1, 50.0 / c1.distance(vectorAlternateSideways));
            // Point corner3 = corner2.add(vectorToward);
            // Point corner4 = corner1.add(vectorToward);
            // System.out.printf("%s - %s - %s, %s - %s - %s", corner1, c1, corner2, corner3, c2, corner4);
            // Path path = new Path(corner1, corner2, corner3, corner4, corner1);

            GraphicsGroup visual = new GraphicsGroup();
            Line line = new Line(c1, c2);
            line.setStrokeColor(BACKGROUND_WHITE);
            visual.add(line);
            switch (roadType) {
                case RoadType.ONE_WAY: {
                    line.setStrokeWidth(4);
                    break;
                }
                case RoadType.TWO_WAY: {
                    line.setStrokeWidth(8);
                    Line divider = new Line(c1, c2);
                    divider.setStrokeColor(HIGHLIGHT_YELLOW);
                    divider.setStrokeWidth(1);
                    visual.add(divider);
                    break;
                }
                case RoadType.HIGHWAY: {
                    line.setStrokeWidth(16);
                    Line divider = new Line(c1, c2);
                    divider.setStrokeColor(HIGHLIGHT_YELLOW);
                    divider.setStrokeWidth(1);
                    visual.add(divider);
                    break;
                }
            }
            road.setVisual(visual);
            this.graphGroup.add(visual);
        };
    }
    
    public void markUnselected(Building b) {
        ((Rectangle)b.getVisual()).setStrokeColor(BUILDING_BLUE);
    }

    public void generateRandomBuildings(int buildings) {
        for (int i = 0; i < buildings; i++) {
            Building building = this.graph.addBuilding(Util.randomEnum(BuildingType.class));
            int size = 50;
            int padding = 10 + size;
            Rectangle rect = new Rectangle(new Point(Util.randomInt(padding, this.canvas.getWidth() - padding), Util.randomInt(padding, this.canvas.getHeight() - padding)), new Point(size, size));
            rect.setFilled(true);
            rect.setFillColor(BUILDING_BLUE);
            rect.setStrokeColor(BUILDING_BLUE);
            rect.setStrokeWidth(2);
            building.setVisual(rect);
            this.graphGroup.add(rect);
        }
    }

    public void updateBudgetBalance() {
        budgetBalance.setText(String.format("$%,d", this.balance));
    }

    public void runSimulation() {}

    public static void main(String[] args) {
        Simulator sim = new Simulator();
    }
}

interface PositionEvent {
    Point getPosition();
}


class PositionEventAdapter implements PositionEvent {
    private final Point position;

    public PositionEventAdapter(MouseButtonEvent e) {
        this.position = e.getPosition();
    }

    public PositionEventAdapter(MouseMotionEvent e) {
        this.position = e.getPosition();
    }

    @Override
    public Point getPosition() {
        return position;
    }
}

interface PositionEventHandler {
    void handleEvent(PositionEvent event);
}