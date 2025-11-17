import java.util.Deque;

public class Car {
    private Building carStart;
    private Building carEnd;

    public Car(Building carStart, Building carEnd) {
        this.carStart = carStart;
        this.carEnd = carEnd;
    }

    public void pathToDestination() {
        Deque<Building> queue= new Deque<Building>();
        for (Road road : carStart.getRoads()) {
            queue.offerFirst(road.roadTo());
        }
    }
}
