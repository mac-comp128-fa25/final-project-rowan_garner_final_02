import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.TreeMap;

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
        Map<Building, Integer> dist = new TreeMap<Building, Integer>() {
            
        };
        for (Building node : theWorld.getBuildings()) {
            dist.put(node, Integer.MAX_VALUE);
        }
        dist.put(carStart, 0);

        PriorityQueue<Building> queue = new PriorityQueue<Building>();
        for (Building node : theWorld.getBuildings()) {
            queue.offer(node);
        }

        while (!queue.isEmpty()) {

        }
    }
}
