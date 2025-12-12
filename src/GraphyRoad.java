import java.awt.Dimension;
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

public class GraphyRoad {
    private static final int DEFAULT_BALANCE = 1000000;
    private CanvasWindow canvas;

    private GraphicsGroup homeScreen = new GraphicsGroup();
    private GraphicsGroup gameScreen = new GraphicsGroup();

    private boolean isInGame;
    private Graph gameGraph;
    private GraphicsGroup gameObjects;
    private GraphicsGroup gameMenu = new GraphicsGroup();
    private Rectangle gameMenuBackground;
    private Button returnHomeButton;
    private Button gameRunButton;
    private int gameBudget;
    private GraphicsText gameBudgetText;
    private ArrayList<Building> selectedBuildings;

    private GraphicsGroup homeObjects = new GraphicsGroup();

    public GraphyRoad() {
        Dimension screen = java.awt.Toolkit.getDefaultToolkit().getScreenSize();
        canvas = new CanvasWindow("Graphy Road", screen.width, screen.height);
        canvas.setBackground(Palette.WHITE);

        GraphicsText homeText = new GraphicsText("Graphy Road");
        homeText.setFont("Departure Mono, Courier New", FontStyle.PLAIN, 80.0);
        homeObjects.add(homeText, (canvas.getWidth() / 2) - (homeText.getWidth() / 2), 100);

        Button playButton = new Button("Play Random Map");
        playButton.onClick(() -> {
            playNewGame();
        });
        homeObjects.add(playButton, (canvas.getWidth() / 2) - (playButton.getWidth() / 2), homeText.getY() + 100);

        homeScreen.add(homeObjects);
        canvas.add(homeScreen);

        canvas.onClick(e ->
            handleMouseClick(new PositionEventAdapter(e))
        );
        canvas.onDrag(e ->
            handleMouseClick(new PositionEventAdapter(e))
        );
    }

    private void playNewGame() {
        isInGame = true;
        canvas.remove(homeScreen);
        gameScreen = new GraphicsGroup();
        gameGraph = new Graph();
        gameObjects = new GraphicsGroup();

        gameBudget = DEFAULT_BALANCE;
        gameBudgetText = new GraphicsText();
        updateBudgetBalance();
        gameBudgetText.setFont(FontStyle.PLAIN, 24);
        gameBudgetText.setFillColor(Palette.GREEN);
        gameScreen.add(gameBudgetText, 10, 30);

        gameRunButton = new Button("Run Simluation");
        gameRunButton.onClick(() -> runSimulation());
        gameScreen.add(gameRunButton, canvas.getWidth() - gameRunButton.getWidth() - 10, 10);

        returnHomeButton = new Button("Back to Main Menu");
        returnHomeButton.onClick(() -> { canvas.remove(gameScreen); canvas.add(homeScreen); isInGame = false; });
        gameScreen.add(returnHomeButton, canvas.getWidth() - gameRunButton.getWidth() - returnHomeButton.getWidth() - 10, 10);

        for (Building building : Map.generateRandomGridLayout(canvas, 100, 60, 50).getBuildings()) {
            this.gameGraph.addBuilding(building);
            this.gameObjects.add(building.draw());
        }
        gameScreen.add(gameObjects);

        Point menuDimensions = new Point(canvas.getWidth() / 3, canvas.getHeight() / 3);
        Point menuAnchor = new Point(canvas.getWidth() - menuDimensions.getX(), menuDimensions.getY() * 2);
        gameMenuBackground = new Rectangle(menuAnchor, menuDimensions);
        gameMenuBackground.setFilled(true);
        gameMenuBackground.setFillColor(Palette.GRAY);
        gameMenuBackground.setStroked(false);

        selectedBuildings = new ArrayList<>();
        canvas.add(gameScreen);
    }

    /**
     * Handle a mouse click event that includes a position.
     * <pre>
     * Did the user...
     *    ___________          ________________                         
     *   ╱           ╲        ╱                ╲    ┌──────────────────┐
     *  ╱ Click on    ╲______╱ Building already ╲___│Deselect building.│
     *  ╲ a building? ╱yes   ╲ selected?        ╱yes└─────────┬────────┘
     *   ╲___________╱        ╲________________╱              │         
     *         │no                    │no                     │         
     *   ______▽_______         ______▽_______                │         
     *  ╱              ╲       ╱              ╲               │         
     * ╱ Holding shift? ╲___  ╱ Holding shift? ╲___           │         
     * ╲                ╱yes│ ╲                ╱yes│          │         
     *  ╲______________╱    │  ╲______________╱    │          │         
     *         │no          │         │no          │          │         
     *  ┌──────▽─────┐      │  ┌──────▽─────┐      │          │         
     *  │Deselect ALL│      │  │Deselect ALL│      │          │         
     *  │buildings.  │      │  │buildings.  │      │          │         
     *  └──────┬─────┘      │  └──────┬─────┘      │          │         
     *         │            │         └──┬─────────┘          │         
     *         │            │   ┌────────▽───────┐            │         
     *         │            │   │Select building.│            │         
     *         │            │   └────────┬───────┘            │         
     *         └────────────┴────────────┴────────────────────┘         
     *            ┌─────────▽─────────┐                                 
     *            │Update construction│                                 
     *            │menus.             │                                 
     *            └───────────────────┘                                 
     * </pre>
     * @param event Mouse event handler event.
     */
    // Graph from https://diagon.arthursonzogni.com/.
    //
    // if ("Clicked on a building?") {
    //   if ("Building already selected?") {
    //     "Deselect building.";
    //   } else {
    //     if ("Holding shift?") {
    //       noop;
    //     } else {
    //       "Deselect ALL buildings.";
    //     }
    //     "Select building.";
    //   }
    // } else {
    //   if ("Holding shift?") {
    //     noop;
    //   } else {
    //     "Deselect ALL buildings.";
    //   }
    // }
    // return "Update construction menus.";
    public void handleMouseClick(PositionEvent event) {
        if (!isInGame) return;

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
            for (Building building : gameGraph.getBuildings()) {
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

    /**
     * Helper method for constructing a new road of a specified type between two specified buildings.
     * Adds the road to the graph, draws it to the canvas graph group, deducts the cost, and updates UI elements.
     */
    private Runnable buildRoadBetween(Building buildingA, Building buildingB, RoadType roadType) {
        return () -> {
            Road road = new Road(buildingA, buildingB, roadType);
            connectRoadToGraph(road);
        };
    }

    /**
     * Helper method for adding a road to the graph, drawing it to the canvas graph group, deducting the cost, and updating UI elements.
     */
    private void connectRoadToGraph(Road road) {
        this.gameGraph.addRoad(road);
        this.gameObjects.add(road.draw());
        double cost = road.getCost();
        gameBudget -= cost;
        updateBudgetBalance();
        updateConstructionMenus();
    }

    /**
     * Helper method for modifying an existing road to a new type.
     * Updates the total budget balance given the new and old cost of the road, redraws the graphic representation, and updates UI elements.
     * 
     * @param road
     * @param type
     * @return
     */
    private Runnable modifyRoad(Road road, RoadType type) {
        return () -> {
            double prevCost = road.getCost();
            road.setType(type);
            double newCost = road.getCost();
            this.gameObjects.remove(road.getGraphicsObject());
            this.gameObjects.add(road.draw());
            gameBudget -= newCost - prevCost;
            updateBudgetBalance();
            updateConstructionMenus();
        };
    }

    private String formatDifference(int old, int current) {
        int diff = old - current;
        String sign = "";
        if (diff > 0) {
            sign += "+";
        } else if (diff < 0) {
            sign += "-";
        }
        return sign + String.format("$%,d", Math.abs(diff));
    }

    /**
     * Update road construction menus for building, modifying, and removing road connections.
     */
    private void updateConstructionMenus() {
        int gap = 5;
        // Clear existing menu options except for the backgrond.
        gameMenu.removeAll();
        gameMenu.add(gameMenuBackground);

        switch (selectedBuildings.size()) {
            case 2:
                Building buildingA = selectedBuildings.get(0);
                Building buildingB = selectedBuildings.get(1);
                Road road = buildingA.getRoadBetween(buildingB, true);

                GraphicsGroup menuOptions = new GraphicsGroup();

                // If the road already exists, it can be modified or removed.
                if (road != null) {
                    GraphicsText modifyMenuLabel = new GraphicsText("Modify Road");
                    modifyMenuLabel.setFillColor(Palette.BLACK);
                    modifyMenuLabel.setFontStyle(FontStyle.BOLD);
                    modifyMenuLabel.setPosition(gameMenuBackground.getX() + gap, gameMenuBackground.getY() + modifyMenuLabel.getHeight() + gap);
                    menuOptions.add(modifyMenuLabel);
                    int oldCost = road.getCost();

                    Button oneWay = new Button("One Way " + formatDifference(oldCost, road.getCost(RoadType.ONE_WAY)));
                    oneWay.setPosition(modifyMenuLabel.getX(), modifyMenuLabel.getY() + modifyMenuLabel.getHeight() + gap);
                    oneWay.onClick(modifyRoad(road, RoadType.ONE_WAY));
                    Button twoWay = new Button("Two Way (Regular) " + formatDifference(oldCost, road.getCost(RoadType.TWO_WAY)));
                    twoWay.setPosition(modifyMenuLabel.getX(), oneWay.getY() + oneWay.getHeight() + gap);
                    twoWay.onClick(modifyRoad(road, RoadType.TWO_WAY));
                    Button highway = new Button("Highway " + formatDifference(oldCost, road.getCost(RoadType.HIGHWAY)));
                    highway.setPosition(modifyMenuLabel.getX(), twoWay.getY() + twoWay.getHeight() + gap);
                    highway.onClick(modifyRoad(road, RoadType.HIGHWAY));

                    // Only add the two relevant buttons (other than the current type).
                    Button[] buttons = switch (road.getType()) {
                        case ONE_WAY -> new Button[] { twoWay, highway };
                        case TWO_WAY -> new Button[] { oneWay, highway };
                        case HIGHWAY -> new Button[] { oneWay, twoWay };
                    };
                    Point relPos = new Point(modifyMenuLabel.getX(), modifyMenuLabel.getY() + modifyMenuLabel.getHeight() + gap);
                    menuOptions.add(buttons[0], relPos.getX(), relPos.getY());
                    menuOptions.add(buttons[1], relPos.getX(), relPos.getY() + buttons[0].getHeight() + 5);

                    GraphicsText removeMenuLabel = new GraphicsText("Remove Road");
                    removeMenuLabel.setFillColor(Palette.BLACK);
                    removeMenuLabel.setFontStyle(FontStyle.BOLD);
                    removeMenuLabel.setPosition(gameMenuBackground.getX() + gap, modifyMenuLabel.getY() + menuOptions.getHeight() + gap);
                    menuOptions.add(removeMenuLabel);
                    Button remove = new Button("Remove " + formatDifference(oldCost, 0));
                    remove.setPosition(removeMenuLabel.getX(), removeMenuLabel.getY() + removeMenuLabel.getHeight() + gap);
                    remove.onClick(() -> {
                        road.roadStart().removeRoad(road);
                        road.roadEnd().removeRoad(road);
                        gameObjects.remove(road.getGraphicsObject());
                        gameBudget += road.getCost();
                        updateBudgetBalance();
                        updateConstructionMenus();
                    });
                    menuOptions.add(remove);
                } else {
                    GraphicsText menuLabel = new GraphicsText("Build Road");
                    menuLabel.setFillColor(Palette.BLACK);
                    menuLabel.setFontStyle(FontStyle.BOLD);
                    menuLabel.setPosition(gameMenuBackground.getX() + gap, gameMenuBackground.getY() + menuLabel.getHeight() + gap);
                    menuOptions.add(menuLabel);

                    Road dummyRoad = new Road(buildingA, buildingB, RoadType.ONE_WAY);
                    Button oneWay = new Button("One Way " + formatDifference(0, dummyRoad.getCost()));
                    oneWay.setPosition(menuLabel.getX(), menuLabel.getY() + menuLabel.getHeight() + gap);
                    oneWay.onClick(buildRoadBetween(buildingA, buildingB, RoadType.ONE_WAY));
                    Button twoWay = new Button("Two Way (Regular) " + formatDifference(0, dummyRoad.getCost(RoadType.TWO_WAY)));
                    twoWay.setPosition(menuLabel.getX(), oneWay.getY() + oneWay.getHeight() + gap);
                    twoWay.onClick(buildRoadBetween(buildingA, buildingB, RoadType.TWO_WAY));
                    Button highway = new Button("Highway " + formatDifference(0, dummyRoad.getCost(RoadType.HIGHWAY)));
                    highway.setPosition(menuLabel.getX(), twoWay.getY() + twoWay.getHeight() + gap);
                    highway.onClick(buildRoadBetween(buildingA, buildingB, RoadType.HIGHWAY));
                    menuOptions.add(oneWay);
                    menuOptions.add(twoWay);
                    menuOptions.add(highway);
                }

                gameMenu.add(menuOptions);
                if (gameMenu.getParent() == null) {
                    gameScreen.add(gameMenu);
                }
                break;
            default:
                if (gameMenu.getParent() != null) {
                    gameScreen.remove(gameMenu);
                }
                break;
        }
    }


    public void unmarkSelectedBuilding(Building building) {
        building.getGraphicsObject().setStrokeColor(building.getGraphicsObject().getFillColor());
    }
    public void markSelectedBuilding(Building building) {
        building.getGraphicsObject().setStrokeColor(Palette.YELLOW);
    }

    public void updateBudgetBalance() {
        gameBudgetText.setText(String.format("$%,d", this.gameBudget));
        gameBudgetText.setFillColor(gameBudget > 0 ? Palette.GREEN : Palette.RED);
    }

    public void runSimulation() {}

    public static void main(String[] args) {
        GraphyRoad sim = new GraphyRoad();
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
