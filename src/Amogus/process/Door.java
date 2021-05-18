package Amogus.process;

import Amogus.utils.PlayerData;
import arc.util.Interval;
import arc.util.Time;
import arc.util.Timer;
import mindustry.Vars;
import mindustry.gen.Call;
import mindustry.world.Tile;

public class Door {

    public static final float COOLDOWN = 60 * 20f;
    public static final int OPEN_ON = 10;
    public Tile tile;
    public float time;

    Interval interval;
    boolean activated = false;

    public Door(Tile tile) {
        this.tile = tile;
        this.time = 0;
        interval = new Interval(1);
    }

    public void update(PlayerData data) {
        short mx = (short) (data.player.mouseX / Vars.tilesize), my = (short) (data.player.mouseY / Vars.tilesize);
        if (!activated && data.player.unit() != null && data.player.unit().isShooting && interval.get(0, 60f)) {
            if (mx >= tile.x - 2 && my >= tile.y - 2 && mx <= tile.x + 3 && my <= tile.y + 3) {
                Call.label(data.player.con, time < 0 ? "[lime]ready" : "[accent]" + Math.floor(time / 60), Time.delta, tile.drawx(), tile.drawy());
            }
        }
        if (time < 0 && !activated && tile.build != null) {
            if (mx >= tile.x && my >= tile.y && mx <= tile.x + 1 && my <= tile.y + 1) {
                tile.build.configure(false);
                if (tile.build.config().equals(false)) {
                    activated = true;
                    Timer.schedule(() -> {
                        time = COOLDOWN;
                        activated = false;
                        tile.build.configure(true);
                    }, OPEN_ON);
                }
            }
        } else {
            time = time - 1;
        }
    }
}
