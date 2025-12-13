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

    /**
     * Draws the graphical/visual representation of this node to a new GraphicsObject.
     * <strong>Implementors should call <code>setGraphicsObject</code> for the new visual internally when called</strong>.
     * @return new graphical representation object
     */
    public abstract T draw();
}
