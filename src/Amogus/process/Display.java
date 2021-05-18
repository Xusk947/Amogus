package Amogus.process;

import Amogus.utils.PlayerData;

public abstract class Display {
    public int x1, y1, x2, y2; // (x, y)1 - start : (x, y)2 - end

    public Display(int x1, int y1, int x2, int y2) {
        this.x1 = x1;
        this.y1 = y1;
        this.x2 = x2;
        this.y2 = y2;
    }
    
    public abstract void update(PlayerData data);
}
