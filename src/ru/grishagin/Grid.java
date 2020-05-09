package ru.grishagin;

public class Grid {

    private Vector2<Integer> size;
    private boolean isVisible;

    public Grid(Vector2<Integer> size) {
        this.size = size;
    }

    public Vector2<Integer> getSize() {
        return size;
    }

    public boolean isVisible() {
        return isVisible;
    }

    public void setVisible(boolean visible) {
        isVisible = visible;
    }
}
