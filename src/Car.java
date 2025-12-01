import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Set;

public class Car {
    private Building carStart;
    private Building carEnd;

    public Car(Building carStart, Building carEnd) {
        this.carStart = carStart;
        this.carEnd = carEnd;
    }

    public void pathToDestination() {
        Set<Building> allNodes = collectAllBuildings(carStart);

        Map<Building, Integer> dist = new HashMap<Building, Integer>();


        for (Building node : allNodes) {
            dist.put(node, Integer.MAX_VALUE);
        }
        dist.put(carStart, 0);


        PriorityQueue<Building> pq = new PriorityQueue<>(
        (a, b) -> Integer.compare(dist.get(a), dist.get(b))
        );
        pq.add(carStart);


        while (!pq.isEmpty()) {
            Building current = pq.poll();

            for (Road r : current.getRoads()) {

                if (!r.roadStart().equals(current)) continue;

                Building next = r.roadEnd();
                int newDist = dist.get(current) + r.getRoadCost();

                if (newDist < dist.get(next)) {
                    dist.put(next, newDist);
                    next.prev(current);
                    next.setPrevRoad(r);
                    pq.add(next);
                }
            }
        }


        ArrayList<Road> finalPath = new ArrayList<Road>();
        Building node = carEnd;
        
        while (node.hasPrev()) {
            finalPath.add(node.getPrevRoad());
            node = node.prev();
        }

        Collections.reverse(finalPath);

        for (Road r : finalPath) {
            r.drive();
        }
    }

    
    // Helper Method
    private Set<Building> collectAllBuildings(Building start) {
        Set<Building> visited = new HashSet<>();
        Deque<Building> stack = new ArrayDeque<>();
        stack.push(start);

        while (!stack.isEmpty()) {
            Building b = stack.pop();
            if (visited.contains(b)) continue;
            visited.add(b);

            for (Road r : b.getRoads()) {
                if (r.roadStart().equals(b)) {
                    stack.push(r.roadEnd());
                }
            }
        }

        return visited;
    }
}
