package Amogus.process;

import Amogus.utils.PlayerData;
import Amogus.utils.Rect;

public abstract class Display {
    public Rect rect;

    public Display(int x1, int y1, int x2, int y2) {
        rect = new Rect(x1, y1, x2, y2);
    }
    
    public abstract void update(PlayerData data);
}
