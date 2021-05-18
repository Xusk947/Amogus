package Amogus.utils;

import arc.struct.Seq;
import arc.util.Timer;

public class DeadBody {
    public static Seq<DeadBody> ALL = new Seq<>();

    public float x, y;
    public PlayerData data;
    public boolean actived = false;

    public DeadBody(float x, float y, PlayerData data) {
        this.x = x;
        this.y = y;
        this.data = data;
        Timer.schedule(() -> actived = true, 2);
        ALL.add(this);
    }
    
    public void remove() {
        ALL.remove(this);
    }
}
