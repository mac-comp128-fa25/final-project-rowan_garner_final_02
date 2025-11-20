import java.util.ArrayDeque;
import java.util.Deque;
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
        Map<Building, Integer> dist = new Map() {
            
        };
        for (Building node : theWorld.getBuildings()) {
            dist[node] = Integer.MAX_VALUE;
        }
        dist[carStart] = 0;

        PriorityQueue<Building> queue = new PriorityQueue<Building>();
        for (Building node : theWorld.getBuildings()) {
            queue.offer(node);
        }

        while (!queue.isEmpty()) {

        }
    }
}
