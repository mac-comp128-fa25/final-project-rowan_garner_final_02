import edu.macalester.graphics.GraphicsObject;

public abstract class Node<T extends GraphicsObject> {
    private T graphicsObject;
    // private boolean isSelected;

    public T getGraphicsObject() {
        return graphicsObject;
    }

    public void setGraphicsObject(T visual) {
        this.graphicsObject = visual;
    }

    // public void setSelected(boolean isSelected) {
    //     this.isSelected = isSelected;
    // }
    
    // public boolean isSelected() {
    //     return isSelected;
    // }

    public abstract T draw();
}
