import java.util.ArrayList;
import java.util.Iterator;

import edu.macalester.graphics.CanvasWindow;
import edu.macalester.graphics.FontStyle;
import edu.macalester.graphics.GraphicsGroup;
import edu.macalester.graphics.GraphicsObject;
import edu.macalester.graphics.GraphicsText;
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

    private GraphicsGroup constructionMenuGroup;
    private Rectangle constructionMenuBackground;

    private Button runButton;
    private GraphicsText budgetBalance;
    private ArrayList<Building> selectedBuildings;

    private int balance = 1000000;

    public Simulator() {
        canvas = new CanvasWindow("Graphy Road", 800, 600);
        canvas.setBackground(Palette.TEXT_BLACK);
        graph = new Graph();

        budgetBalance = new GraphicsText();
        updateBudgetBalance();
        budgetBalance.setFont(FontStyle.PLAIN, 24);
        budgetBalance.setFillColor(Palette.MONEY_GREEN);
        canvas.add(budgetBalance, 10, 30);

        runButton = new Button("Run Simluation");
        runButton.onClick(() -> runSimulation());
        canvas.add(runButton, canvas.getWidth() - runButton.getWidth() - 10, 10);

        graphGroup = new GraphicsGroup();
        for (Building building : Map.BASIC_LAYOUT.getBuildings()) {
            this.graph.addBuilding(building);
            this.graphGroup.add(building.draw());
        }
        canvas.add(graphGroup);

        constructionMenuGroup = new GraphicsGroup();
        Point menuDimensions = new Point(canvas.getWidth() / 4, canvas.getHeight() / 3);
        Point menuAnchor = new Point(canvas.getWidth() - canvas.getWidth() / 4, (canvas.getHeight() / 3) * 2);
        constructionMenuBackground = new Rectangle(menuAnchor, menuDimensions);
        constructionMenuBackground.setFilled(true);
        constructionMenuBackground.setFillColor(Palette.SURFACE_GRAY);
        constructionMenuBackground.setStroked(false);

        selectedBuildings = new ArrayList<>();
        canvas.onClick(e ->
            handleMouseClick(new PositionEventAdapter(e))
        );
        canvas.onDrag(e ->
            handleMouseClick(new PositionEventAdapter(e))
        );
    }

    public void handleMouseClick(PositionEvent event) {
        GraphicsObject el = canvas.getElementAt(event.getPosition());
        boolean isHoldingShift = canvas.getKeysPressed().contains(Key.SHIFT);

        if (el == null && !isHoldingShift) {
            // Clicked outside of buildings clears selection.
            Iterator<Building> iter = selectedBuildings.iterator();
            while (iter.hasNext()) {
                Building building = iter.next();
                unmarkSelectedBuilding(building);
                iter.remove();
            }
        } else if (el instanceof Rectangle rect) {
            // Scan buildings for a match with the selected visual.
            for (Building building : graph.getBuildings()) {
                if (building.getGraphicsObject().equals(rect)) {
                    // Match found!
                    // Check if the building is already selected.
                    if (selectedBuildings.remove(building)) {
                        // If it was selected, update its status to unselected.
                        unmarkSelectedBuilding(building);
                    } else {
                        // It wasn't selected already, so we are adding and marking it.
                        if (!isHoldingShift) {
                            // // If the user wasn't holding shift, we clear the existing selection (and later add just the new building), effectively a selection replacement.
                            Iterator<Building> iter = selectedBuildings.iterator();
                            while (iter.hasNext()) {
                                Building selectedBuilding = iter.next();
                                unmarkSelectedBuilding(selectedBuilding);
                                iter.remove();
                            }
                        }
                        // Add the building to selection.
                        selectedBuildings.add(building);
                        markSelectedBuilding(building);
                    }
                    break;
                }
            }
        }
        updateConstructionMenus();
    }

    private Runnable buildRoadBetween(Building buildingA, Building buildingB, RoadType roadType) {
        return () -> {
            Road road = this.graph.addRoad(buildingA, buildingB, roadType);
            this.graphGroup.add(road.draw());
            updateConstructionMenus();
        };
    }

    private Runnable modifyRoad(Road road, RoadType type) {
        return () -> {
            road.setType(type);
            this.graphGroup.remove(road.getGraphicsObject());
            this.graphGroup.add(road.draw());
            updateConstructionMenus();
        };
    }

    private void updateConstructionMenus() {
        int gap = 5;
        constructionMenuGroup.removeAll();
        constructionMenuGroup.add(constructionMenuBackground);
        switch (selectedBuildings.size()) {
            case 2:
                Building buildingA = selectedBuildings.get(0);
                Building buildingB = selectedBuildings.get(1);
                Road road = buildingA.getRoadBetween(buildingB, true);

                GraphicsGroup menuOptions = new GraphicsGroup();

                if (road != null) {
                    GraphicsText modifyMenuLabel = new GraphicsText("Modify Road");
                    modifyMenuLabel.setFillColor(Palette.BACKGROUND_WHITE);
                    modifyMenuLabel.setPosition(constructionMenuBackground.getX() + gap, constructionMenuBackground.getY() + modifyMenuLabel.getHeight() + gap);
                    menuOptions.add(modifyMenuLabel);

                    Button oneWay = new Button("One Way");
                    oneWay.setPosition(modifyMenuLabel.getX(), modifyMenuLabel.getY() + modifyMenuLabel.getHeight() + gap);
                    oneWay.onClick(modifyRoad(road, RoadType.ONE_WAY));
                    Button twoWay = new Button("Two Way (Regular)");
                    twoWay.setPosition(modifyMenuLabel.getX(), oneWay.getY() + oneWay.getHeight() + gap);
                    twoWay.onClick(modifyRoad(road, RoadType.TWO_WAY));
                    Button highway = new Button("Highway");
                    highway.setPosition(modifyMenuLabel.getX(), twoWay.getY() + twoWay.getHeight() + gap);
                    highway.onClick(modifyRoad(road, RoadType.HIGHWAY));

                    Point relPos = new Point(modifyMenuLabel.getX(), modifyMenuLabel.getY() + modifyMenuLabel.getHeight() + gap);
                    switch (road.getType()) {
                        case RoadType.ONE_WAY:
                            stackButtonsBelow(menuOptions, relPos, twoWay, highway);
                            break;
                        case RoadType.TWO_WAY:
                            stackButtonsBelow(menuOptions, relPos, oneWay, highway);
                            break;
                        case RoadType.HIGHWAY:
                            stackButtonsBelow(menuOptions, relPos, oneWay, twoWay);
                            break;
                        default:
                            break;
                    }

                    GraphicsText removeMenuLabel = new GraphicsText("Remove Road");
                    removeMenuLabel.setFillColor(Palette.BACKGROUND_WHITE);
                    removeMenuLabel.setPosition(constructionMenuBackground.getX() + gap, modifyMenuLabel.getY() + menuOptions.getHeight() + gap);
                    menuOptions.add(removeMenuLabel);
                    Button remove = new Button("Remove");
                    remove.setPosition(removeMenuLabel.getX(), removeMenuLabel.getY() + removeMenuLabel.getHeight() + gap);
                    remove.onClick(() -> {
                        road.roadStart().removeRoad(road);
                        road.roadEnd().removeRoad(road);
                        graphGroup.remove(road.getGraphicsObject());
                        updateConstructionMenus();
                    });
                    menuOptions.add(remove);
                } else {
                    GraphicsText menuLabel = new GraphicsText("Build Road");
                    menuLabel.setFillColor(Palette.BACKGROUND_WHITE);
                    menuLabel.setPosition(constructionMenuBackground.getX() + gap, constructionMenuBackground.getY() + menuLabel.getHeight() + gap);
                    menuOptions.add(menuLabel);

                    Button oneWay = new Button("One Way");
                    oneWay.setPosition(menuLabel.getX(), menuLabel.getY() + menuLabel.getHeight() + gap);
                    oneWay.onClick(buildRoadBetween(buildingA, buildingB, RoadType.ONE_WAY));
                    Button twoWay = new Button("Two Way (Regular)");
                    twoWay.setPosition(menuLabel.getX(), oneWay.getY() + oneWay.getHeight() + gap);
                    twoWay.onClick(buildRoadBetween(buildingA, buildingB, RoadType.TWO_WAY));
                    Button highway = new Button("Highway");
                    highway.setPosition(menuLabel.getX(), twoWay.getY() + twoWay.getHeight() + gap);
                    highway.onClick(buildRoadBetween(buildingA, buildingB, RoadType.HIGHWAY));
                    menuOptions.add(oneWay);
                    menuOptions.add(twoWay);
                    menuOptions.add(highway);
                }

                constructionMenuGroup.add(menuOptions);
                if (constructionMenuGroup.getParent() == null) {
                    canvas.add(constructionMenuGroup);
                }
                break;
            default:
                if (constructionMenuGroup.getParent() != null) {
                    canvas.remove(constructionMenuGroup);
                }
                break;
        }
    }

    private void stackButtonsBelow(GraphicsGroup g, Point startPos, Button a, Button b) {
        g.add(a, startPos.getX(), startPos.getY());
        g.add(b, startPos.getX(), startPos.getY() + a.getHeight() + 5);
    }

    public void unmarkSelectedBuilding(Building building) {
        building.getGraphicsObject().setStrokeColor(Palette.BUILDING_BLUE);
    }
    public void markSelectedBuilding(Building building) {
        building.getGraphicsObject().setStrokeColor(Palette.HIGHLIGHT_YELLOW);
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
