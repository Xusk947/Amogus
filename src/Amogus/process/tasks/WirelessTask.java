package Amogus.process.tasks;

import Amogus.process.TaskX;
import Amogus.utils.Crewmate;
import Amogus.utils.M;
import arc.graphics.Color;
import arc.util.Time;
import mindustry.content.Fx;
import mindustry.gen.Call;
import mindustry.world.Tile;

public class WirelessTask extends TaskX {

    float timer = 0;

    public WirelessTask(Tile tile) {
        super(tile);
    }

    @Override
    public void update(Crewmate data) {
        if (timer < 0) {
            M.line(data.cx, data.cy, data.player.mouseX, data.player.mouseY, (x, y) -> {
                if (x % 7 <= 0) {
                    Call.effect(data.player.con, Fx.heal, x, y, 0, Color.gray);
                }
            });
            Call.effect(data.player.con, Fx.bubble, data.cx, data.cy, 0, Color.gray);
            timer = 30;
        } else {
            timer -= Time.delta;
        }
    }

    @Override
    public void onStart(Crewmate data) {
        data.ctime = 0;
        data.cx = 0;
        data.cy = 0;
    }

    @Override
    public TaskX start() {
        return new WirelessTask(tile);
    }
}
