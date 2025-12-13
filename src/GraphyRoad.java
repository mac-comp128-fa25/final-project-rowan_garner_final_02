import java.awt.Dimension;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Iterator;

import edu.macalester.graphics.CanvasWindow;
import edu.macalester.graphics.FontStyle;
import edu.macalester.graphics.GraphicsGroup;
import edu.macalester.graphics.GraphicsObject;
import edu.macalester.graphics.GraphicsText;
import edu.macalester.graphics.Point;
import edu.macalester.graphics.Rectangle;
import edu.macalester.graphics.events.MouseButtonEvent;
import edu.macalester.graphics.events.MouseMotionEvent;
import edu.macalester.graphics.ui.Button;

public class GraphyRoad {
    private static final int DEFAULT_BALANCE = 1000000;
    private CanvasWindow canvas;

    private GraphicsGroup homeScreen = new GraphicsGroup();
    private GraphicsGroup helpScreen = new GraphicsGroup();
    private GraphicsGroup gameScreen;

    private boolean isInGame = false;
    private Graph gameGraph;
    private GraphicsGroup gameObjects;
    private GraphicsGroup gameMenu = new GraphicsGroup();
    private Rectangle gameMenuBackground;
    private Button gameRunButton;
    private Button gameRestartButton;
    private Button gameExitButton;
    private int gameBudget;
    private GraphicsText gameBudgetText;
    private Deque<Building> selectedBuildings;
    private GraphicsText happinessLabel;
    private int maxHappinessScore = 0;

    private GraphicsGroup homeObjects = new GraphicsGroup();
    private GraphicsGroup homePlayButton = new GraphicsGroup();

    public GraphyRoad() {
        /* Home Screen */
        Dimension screen = java.awt.Toolkit.getDefaultToolkit().getScreenSize();
        canvas = new CanvasWindow("Graphy Road", screen.width, screen.height);
        canvas.setBackground(Palette.BACKGROUND_GREEN);

        GraphicsText homeText = new GraphicsText("Graphy Road");
        homeText.setFillColor(Palette.FG_WHITE);
        homeText.setFont("Departure Mono, Courier New", FontStyle.PLAIN, 140.0);
        homeObjects.add(homeText, (canvas.getWidth() / 2) - (homeText.getWidth() / 2), (canvas.getHeight() / 2) - homeText.getHeight());

        Rectangle homePlayButtonBackground = new Rectangle(canvas.getCenter().getX() - 200, homeText.getY() + 200, 400, 200);
        homePlayButtonBackground.setFillColor(Palette.ON_GREEN_GREEN);
        homePlayButtonBackground.setStroked(false);
        GraphicsText playButtonText = new GraphicsText("PLAY");
        playButtonText.setFont("Departure Mono, Courier New", FontStyle.PLAIN, 80.0);
        playButtonText.setFillColor(Palette.FG_WHITE);
        playButtonText.setPosition(homePlayButtonBackground.getCenter().getX() - (playButtonText.getWidth() / 2), homePlayButtonBackground.getCenter().getY() + (playButtonText.getHeight() / 3));
        homePlayButton.add(homePlayButtonBackground);
        homePlayButton.add(playButtonText);
        homeObjects.add(homePlayButton);

        Button helpButton = new Button("Instructions");
        helpButton.setPosition((canvas.getWidth() / 2) - (helpButton.getWidth() / 2), homePlayButtonBackground.getY() + homePlayButtonBackground.getHeight() + 50);
        helpButton.onClick(() -> { canvas.remove(homeScreen); canvas.add(helpScreen); });
        homeObjects.add(helpButton);

        homeScreen.add(homeObjects);
        canvas.add(homeScreen);

        /* Help/Instructions Screen */
        helpScreen = new GraphicsGroup();

        GraphicsText helpTitle = new GraphicsText("Instructions");
        helpTitle.setFillColor(Palette.FG_WHITE);
        helpTitle.setFont("Departure Mono, Courier New", FontStyle.PLAIN, 50.0);
        helpScreen.add(helpTitle, (canvas.getWidth() / 2) - (helpTitle.getWidth() / 2), (canvas.getHeight() / 3) - helpTitle.getHeight());

        GraphicsText helpText = new GraphicsText("The objective of the game is to use your limited city budget to build the optimally connected city layout.\nChoose between different types of roads carefully, as some will especially hurt the bank!\nBuildings can be either residential or commercial.\n  -> Residential buildings are BLUE, commercial buildings are GRAY.\nSelect buildings by clicking on them, and construct with the menu that appears in the bottom right.\nRemoving, downgrading, or upgrading a road will refund or charge your balance\n  to the difference between the old and new cost.\nRestart the game with a new map and connections with the button in the top right while playing.");
        helpText.setFillColor(Palette.FG_WHITE);
        helpText.setFont("Departure Mono, Courier New", FontStyle.PLAIN, 20.0);
        helpScreen.add(helpText, (canvas.getWidth() / 2) - (helpText.getWidth() / 2), helpTitle.getY() + 100);

        Button returnToTitle = new Button("Back");
        returnToTitle.onClick(() -> { canvas.remove(helpScreen); canvas.add(homeScreen); });
        helpScreen.add(returnToTitle, canvas.getWidth() - returnToTitle.getWidth() - 10, 10);

        canvas.onClick(e ->
            handleMouseClick(new PositionEventAdapter(e))
        );
        canvas.onDrag(e ->
            handleMouseClick(new PositionEventAdapter(e))
        );
    }

    private void playNewGame() {
        if (!isInGame) {
            isInGame = true;
            canvas.remove(homeScreen);
        } else {
            canvas.remove(gameScreen);
        }
        gameScreen = new GraphicsGroup();
        gameGraph = new Graph();
        gameObjects = new GraphicsGroup();

        gameBudget = DEFAULT_BALANCE;
        gameBudgetText = new GraphicsText();
        updateBudgetBalance();
        gameBudgetText.setFont(FontStyle.PLAIN, 24);
        gameBudgetText.setFillColor(Palette.FG_WHITE);
        gameScreen.add(gameBudgetText, 10, 30);

        gameExitButton = new Button("Exit to Main Menu");
        gameExitButton.onClick(() -> { canvas.remove(gameScreen); canvas.add(homeScreen); isInGame = false; });
        gameScreen.add(gameExitButton, canvas.getWidth() - gameExitButton.getWidth() - 10, 10);

        gameRestartButton = new Button("Restart");
        gameRestartButton.onClick(() -> {
            playNewGame();
        });
        gameScreen.add(gameRestartButton, canvas.getWidth() - gameExitButton.getWidth() - gameRestartButton.getWidth() - 10, 10);

        gameRunButton = new Button("Run");
        gameRunButton.onClick(() -> runSimulation());
        gameScreen.add(gameRunButton, canvas.getWidth() - gameExitButton.getWidth() - gameRestartButton.getWidth() - gameRunButton.getWidth() - 10, 10);

        for (Building building : Map.generateRandomGridLayout(canvas, 100, 70, 50).getBuildings()) {
            this.gameGraph.addBuilding(building);
            this.gameObjects.add(building.draw());
        }
        gameScreen.add(gameObjects);

        Point menuDimensions = new Point(canvas.getWidth() / 5, canvas.getHeight() / 3);
        Point menuAnchor = new Point(canvas.getWidth() - menuDimensions.getX(), menuDimensions.getY() * 2);
        gameMenuBackground = new Rectangle(menuAnchor, menuDimensions);
        gameMenuBackground.setFilled(true);
        gameMenuBackground.setFillColor(Palette.BG_GRAY);
        gameMenuBackground.setStroked(false);

        happinessLabel = new GraphicsText("Happiness: __");
        happinessLabel.setFont(FontStyle.PLAIN, 24);
        happinessLabel.setFillColor(Palette.FG_WHITE);
        gameScreen.add(happinessLabel, 10, 60);

        selectedBuildings = new ArrayDeque<>(2);
        canvas.add(gameScreen);
    }

    /**
     * Handle a mouse click event that includes a position.
     * <ol>
     * <li>Clears selected buildings if the user clicks on empty canvas.</li>
     * <li>Deselects buildings that are currently selected.</li>
     * <li>Selects new buildings and removes older selection entries (<strong>two buildings can be selected at a time</strong>).</li>
     * </ol>
     * 
     */
    public void handleMouseClick(PositionEvent event) {
        GraphicsObject el = canvas.getElementAt(event.getPosition());

        if (!isInGame) {
            if (el.getParent().equals(homePlayButton)) {
                playNewGame();
            }
        } else {
            if (el == null) {
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
                            if (selectedBuildings.size() > 1) {
                                Building oldLast = selectedBuildings.pollLast();
                                if (oldLast != null) {
                                    unmarkSelectedBuilding(oldLast);
                                }
                            }
                            // Add the building to selection.
                            selectedBuildings.addFirst(building);
                            markSelectedBuilding(building);
                        }
                        break;
                    }
                }
            }
            updateConstructionMenus();
        }
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
            // TODO: Changing road type affects where road is connected? E.g. one way to a two way, need to add connection in other direction. Two way / highway to a one way, need to remove connection to other direction.
            double newCost = road.getCost();
            this.gameObjects.remove(road.getGraphicsObject());
            this.gameObjects.add(road.draw());
            gameBudget -= newCost - prevCost;
            updateBudgetBalance();
            updateConstructionMenus();
        };
    }

    /**
     * Formats the difference between an old and a new cost as an addition or subtraction from the budget balance.
     * @param old old cost
     * @param current new cost
     * @return formatted string in the format "-/+$<num>".
     */
    private String formatBudgetDifference(int old, int current) {
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
                Building buildingA = selectedBuildings.getLast();
                Building buildingB = selectedBuildings.getFirst();
                Road road = buildingA.getRoadBetween(buildingB, true);

                GraphicsGroup menuOptions = new GraphicsGroup();

                // If the road already exists, it can be modified or removed.
                if (road != null) {
                    GraphicsText modifyMenuLabel = new GraphicsText("Modify Road");
                    modifyMenuLabel.setFillColor(Palette.FG_BLACK);
                    modifyMenuLabel.setFontStyle(FontStyle.BOLD);
                    modifyMenuLabel.setPosition(gameMenuBackground.getX() + gap, gameMenuBackground.getY() + modifyMenuLabel.getHeight() + gap);
                    menuOptions.add(modifyMenuLabel);
                    int oldCost = road.getCost();

                    Button oneWay = new Button("One Way " + formatBudgetDifference(oldCost, road.getCost(RoadType.ONE_WAY)));
                    oneWay.setPosition(modifyMenuLabel.getX(), modifyMenuLabel.getY() + modifyMenuLabel.getHeight() + gap);
                    oneWay.onClick(modifyRoad(road, RoadType.ONE_WAY));
                    Button twoWay = new Button("Two Way " + formatBudgetDifference(oldCost, road.getCost(RoadType.TWO_WAY)));
                    twoWay.setPosition(modifyMenuLabel.getX(), oneWay.getY() + oneWay.getHeight() + gap);
                    twoWay.onClick(modifyRoad(road, RoadType.TWO_WAY));
                    Button highway = new Button("Highway " + formatBudgetDifference(oldCost, road.getCost(RoadType.HIGHWAY)));
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
                    removeMenuLabel.setFillColor(Palette.FG_BLACK);
                    removeMenuLabel.setFontStyle(FontStyle.BOLD);
                    removeMenuLabel.setPosition(gameMenuBackground.getX() + gap, modifyMenuLabel.getY() + menuOptions.getHeight() + gap);
                    menuOptions.add(removeMenuLabel);
                    Button remove = new Button("Remove " + formatBudgetDifference(oldCost, 0));
                    remove.setPosition(removeMenuLabel.getX(), removeMenuLabel.getY() + removeMenuLabel.getHeight() + gap);
                    remove.onClick(() -> {
                        road.getEnd().removeRoad(road);
                        road.getStart().removeRoad(road);
                        gameObjects.remove(road.getGraphicsObject());
                        gameBudget += oldCost;
                        updateBudgetBalance();
                        updateConstructionMenus();
                    });
                    menuOptions.add(remove);
                } else {
                    GraphicsText menuLabel = new GraphicsText("Build Road");
                    menuLabel.setFillColor(Palette.FG_BLACK);
                    menuLabel.setFontStyle(FontStyle.BOLD);
                    menuLabel.setPosition(gameMenuBackground.getX() + gap, gameMenuBackground.getY() + menuLabel.getHeight() + gap);
                    menuOptions.add(menuLabel);

                    Road dummyRoad = new Road(buildingA, buildingB, RoadType.ONE_WAY);
                    Button oneWay = new Button("One Way " + formatBudgetDifference(0, dummyRoad.getCost()));
                    oneWay.setPosition(menuLabel.getX(), menuLabel.getY() + menuLabel.getHeight() + gap);
                    oneWay.onClick(buildRoadBetween(buildingA, buildingB, RoadType.ONE_WAY));
                    Button twoWay = new Button("Two Way " + formatBudgetDifference(0, dummyRoad.getCost(RoadType.TWO_WAY)));
                    twoWay.setPosition(menuLabel.getX(), oneWay.getY() + oneWay.getHeight() + gap);
                    twoWay.onClick(buildRoadBetween(buildingA, buildingB, RoadType.TWO_WAY));
                    Button highway = new Button("Highway " + formatBudgetDifference(0, dummyRoad.getCost(RoadType.HIGHWAY)));
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
        building.getGraphicsObject().setStrokeColor(Palette.HIGHLIGHT_YELLOW);
    }

    public void updateBudgetBalance() {
        gameBudgetText.setText(String.format("$%,d", this.gameBudget));
        gameBudgetText.setFillColor(gameBudget > 0 ? Palette.FG_WHITE : Palette.NEGATIVE_RED);
    }

    public void runSimulation() {
        var buildings = gameGraph.getBuildings();
        if (buildings.size() < 2) return;
        maxHappinessScore = 100;

        new Thread(() -> {
            Long endTime = System.currentTimeMillis() + 10000;

            while (System.currentTimeMillis() < endTime) {
                Building start = buildings.get((int)(Math.random() * buildings.size()));
                Building end   = buildings.get((int)(Math.random() * buildings.size()));

                if (start != end) {
                    Car car = new Car(start, end);

                    new Thread(() -> {
                        car.pathToDestination();
                        canvas.draw();

                        int currentScore = happinessScore();

                        if (currentScore < maxHappinessScore) {
                            maxHappinessScore = currentScore;
                        }
                        
                        happinessLabel.setText("Happiness: " + currentScore + " | Max: " + maxHappinessScore);

                    }).start();
                }
                try {
                    Thread.sleep(300);
                } catch (InterruptedException e) {}
            }
        }).start();
    }

    public int happinessScore() { 
        Double total = 0.0; 
        Integer roadCount = 0; 
        for (Road road : gameGraph.getRoads()) { 
            total += Math.pow(road.getRoadCost(), 2); 
            roadCount += 1; 
        } 
        if (2000/(int) (total/roadCount) > 100) {return 100;} else {return 2000/(int) (total/roadCount);}
    }

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
