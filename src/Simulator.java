import java.awt.Color;

import edu.macalester.graphics.CanvasWindow;
import edu.macalester.graphics.FontStyle;
import edu.macalester.graphics.GraphicsGroup;
import edu.macalester.graphics.GraphicsText;
import edu.macalester.graphics.Point;
import edu.macalester.graphics.Rectangle;
import edu.macalester.graphics.ui.Button;

public class Simulator {
    private CanvasWindow canvas;
    private Graph graph;

    private GraphicsGroup graphGroup;

    private GraphicsGroup menuGroup;

    private Button runButton;
    private GraphicsText budgetBalance;

    private int balance = 1000000;

    public Simulator() {
        canvas = new CanvasWindow("Traffic Simulator", 800, 600);
        graph = new Graph();

        budgetBalance = new GraphicsText();
        updateBudgetBalance();
        budgetBalance.setFont(FontStyle.PLAIN, 24);
        budgetBalance.setFillColor(new Color(33, 110, 69));
        canvas.add(budgetBalance, 10, 30);

        runButton = new Button("Run Simluation");
        runButton.onClick(() -> runSimulation());
        canvas.add(runButton, canvas.getWidth() - runButton.getWidth() - 10, 10);


        graphGroup = new GraphicsGroup();
        generateRandomBuildings(10);
        canvas.add(graphGroup);

        // TODO: Add road building menus (build new, modify/delete).
        //  TODO: Handle selecting builings (up to two, highlight on UI, change menus accordingly).

        menuGroup = new GraphicsGroup();

    }

    public void generateRandomBuildings(int buildings) {
        for (int i = 0; i < buildings; i++) {
            Building building = this.graph.addBuilding(Util.randomEnum(BuildingType.class));
            int size = 50;
            int padding = 10 + size;
            Rectangle rect = new Rectangle(new Point(Util.randomInt(padding, this.canvas.getWidth() - padding), Util.randomInt(padding, this.canvas.getHeight() - padding)), new Point(size, size));
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
