package Amogus.utils;

public class Rect {
    public int x, y, endX, endY;
    
    /**
     * In tile pos
     * @param x from
     * @param y from
     * @param endX to
     * @param endY to
     */
    public Rect(int x, int y, int endX, int endY) {
        this.x = x;
        this.y =y;
        this.endX = endX;
        this.endY = endY;
    }
    
    public boolean in(int x, int y) {
        return this.x > x && this.y > y && this.x < endX && this.y < endY;
    }
}
