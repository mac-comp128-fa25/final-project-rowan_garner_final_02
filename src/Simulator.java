import java.awt.Color;
import java.util.Deque;
import java.util.function.Consumer;

import edu.macalester.graphics.CanvasWindow;
import edu.macalester.graphics.FontStyle;
import edu.macalester.graphics.GraphicsGroup;
import edu.macalester.graphics.GraphicsText;
import edu.macalester.graphics.Point;
import edu.macalester.graphics.ui.Button;
import edu.macalester.graphics.ui.TextField;

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

        // TODO: Render buildings and road connections.
        //  TODO: Generate random building maps?

        graphGroup = new GraphicsGroup();

        // TODO: Add road building menus (build new, modify/delete).
        //  TODO: Handle selecting builings (up to two, highlight on UI, change menus accordingly).

        menuGroup = new GraphicsGroup();

    }

    public void updateBudgetBalance() {
        budgetBalance.setText(String.format("$%,d", this.balance));
    }

    public void runSimulation() {}

    public static void main(String[] args) {
        Simulator sim = new Simulator();
    }
}
