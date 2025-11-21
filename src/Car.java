import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.HashMap;
import java.util.Map;
import java.util.PriorityQueue;

public class Car {
    private Building carStart;
    private Building carEnd;
    private Graph theWorld;

    public Car(Building carStart, Building carEnd, Graph theWorld) {
        this.carStart = carStart;
        this.carEnd = carEnd;
        this.theWorld = theWorld;
    }

    public void pathToDestination() {
        Map<Building, Integer> dist = new HashMap<Building, Integer>();


        for (Building node : theWorld.getBuildings()) {
            dist.put(node, Integer.MAX_VALUE);
        }
        dist.put(carStart, 0);


        PriorityQueue<Building> uncheckedNodes = new PriorityQueue<Building>(); // Create Comparator that makes Buildings be prioritized by dist.get(Building)
        for (Building node : theWorld.getBuildings()) {
            uncheckedNodes.add(node);
        }


        while (!uncheckedNodes.isEmpty()) {
            Building bestNode = uncheckedNodes.poll();


            for (Road road : bestNode.getRoads()) {
                if (road.getRoadCost() + dist.get(bestNode) < dist.get(road.roadTo())) {
                    dist.replace(road.roadTo(), road.getRoadCost() + dist.get(bestNode));
                    road.roadTo().prev();
                }
            }
        }


        ArrayList<Road> finalPath = new ArrayList<Road>();
        Building node = carEnd;
        while (node.hasPrev()) {
            finalPath.add(node.prev().roadBetween(node)); // pretty sure this checks in the right direction :)
            node = node.prev();
        }


        for (Road road : finalPath) {
            road.drive();
        }
    }

}
