import edu.macalester.graphics.FontStyle;
import edu.macalester.graphics.GraphicsGroup;
import edu.macalester.graphics.GraphicsObject;
import edu.macalester.graphics.GraphicsText;
import edu.macalester.graphics.Line;
import edu.macalester.graphics.Point;

public class Road extends Node<GraphicsObject> {
    private RoadType type;
    private Building a;
    private Building b;
    private int carCount;
    private GraphicsText costLabel;

    public Road(Building a, Building b, RoadType type) {
        this.a = a;
        this.b = b;
        this.type = type;
        carCount = 0;
    }

    public RoadType getType() {
        return type;
    }

    public void setType(RoadType type) {
        this.type = type;
    }

    public GraphicsGroup draw() {
        Point c1 = a.getGraphicsObject().getCenter();
        Point c2 = b.getGraphicsObject().getCenter();

        GraphicsGroup visual = new GraphicsGroup();
        Line line = new Line(c1, c2);
        line.setStrokeColor(Palette.ROAD_GRAY);
        visual.add(line);
        switch (type) {
            case RoadType.ONE_WAY: {
                line.setStrokeWidth(4);
                break;
            }
            case RoadType.TWO_WAY: {
                line.setStrokeWidth(8);
                Line divider = new Line(c1, c2);
                divider.setStrokeColor(Palette.HIGHLIGHT_YELLOW);
                divider.setStrokeWidth(1);
                visual.add(divider);
                break;
            }
            case RoadType.HIGHWAY: {
                line.setStrokeWidth(16);
                Line divider = new Line(c1, c2);
                divider.setStrokeColor(Palette.HIGHLIGHT_YELLOW);
                divider.setStrokeWidth(1);
                visual.add(divider);
                break;
            }
        }
        double midX = (c1.getX() + c2.getX()) / 2;
        double midY = (c1.getY() + c2.getY()) / 2;

        if (costLabel == null) {
            costLabel = new GraphicsText();
            costLabel.setFillColor(Palette.TESTING_GREEN);
            costLabel.setFont(FontStyle.BOLD, 14);
        } else {
            costLabel.getParent().remove(costLabel);
        }

        costLabel.setText(String.format("%.1f", getRoadCost()));
        visual.add(costLabel, midX - costLabel.getWidth()/2, midY - costLabel.getHeight()/2);

        this.setGraphicsObject(visual);
        return visual;
    }

    /**
     * Check if this road is connecting two specified buildings.
     */
    public boolean isConnecting(Building a, Building b) {
        return (a.equals(this.a) && b.equals(this.b)) || (a.equals(this.b) && b.equals(this.a));
    }

    public double getDistance() {
        return a.getLocation().distance(b.getLocation());
    }

    /**
     * Calculate the construction cost for this road, including type and distance.
     */
    public int getCost() {
        return getCost(this.type);
    }

    public int getCost(RoadType type) {
        double distance = getDistance();
        double cost = type.cost;

        return (int) (distance * cost * 3);
    }

    public Building roadEnd() {
        return b;
    }

    public Building roadStart() {
        return a;
    }

    public void drive() {
        carCount += 1;
        updateCostLabel();

        double delay = getRoadCost()*1000;

        new Thread(() -> {
            try {
                Thread.sleep((long) delay);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            carCount -= 1;
            updateCostLabel();
        }).start();
    }

    public void updateCostLabel() {
        if (costLabel != null) {
            costLabel.setText(String.format("%.1f", getRoadCost()));
        }
    }

    public double getRoadCost() {
        if (this.type == RoadType.HIGHWAY) {
            return (carCount/2 + 2) * 1;
        }
        return (carCount + 2) * 1.5;
    }
}
