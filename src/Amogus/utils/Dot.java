package Amogus.utils;

import mindustry.Vars;

public class Dot {
    public float x, y;
    
    public Dot(float x, float y) {
        this.x = x;
        this.y = y;
    }
    
    public boolean in(float x1, float y1, int dst) {
        int t = dst * Vars.tilesize;
        return x1 > x - t && x1 < x + t && y1 > y - t && y1 < y + t;
    }
}
