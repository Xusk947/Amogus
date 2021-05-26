package Amogus.process.tasks;

import Amogus.process.TaskX;
import Amogus.utils.Crewmate;
import Amogus.utils.Dot;
import Amogus.utils.M;
import arc.graphics.Color;
import arc.math.Mathf;
import arc.util.Timer;
import mindustry.Vars;
import mindustry.content.Fx;
import mindustry.gen.Call;
import mindustry.world.Tile;

public class GarbageTask extends TaskX {

    public Dot from, to;
    public int timer = 0;
    public boolean sended = false;

    public GarbageTask(Tile tile) {
        super(tile);
        this.from = new Dot(tile.drawx(), tile.drawy() + Vars.tilesize * 3);
        this.to = new Dot(tile.drawx(), tile.drawy() - Vars.tilesize * 3);
    }

    @Override
    public void update(Crewmate data) {
        if (sended && timer % 2 <= 0) {
            Call.effect(Fx.bubble, tile.drawx() + Mathf.random(-16, 16), tile.drawy() + Mathf.random(-16, 16), Mathf.random(360), Color.forest);
        }
        if (timer < 0) {
            timer = 60;
            Call.effect(data.player.con, Fx.pointHit, from.x, from.y, 0, Color.red);
            Call.effect(data.player.con, Fx.pointHit, to.x, to.y, 0, Color.yellow);
            M.line(data.cx, data.cy, data.player.mouseX, data.player.mouseY, (x, y) -> {
                if (x % 10 <= 0) {
                    Call.effect(data.player.con, Fx.pointHit, x, y, 0, Color.gray);
                }
            });
            if (!sended && from.in(data.cx, data.cy, 16) && to.in(data.player.mouseX, data.player.mouseY, 16)) {
                Timer.schedule(() -> {
                    onFinish(data);
                }, 2);
                sended = true;
            }
        } else {
            timer -= 1;
        }
    }

    @Override
    public void onStart(Crewmate data) {
        resc(data);
    }

    @Override
    public TaskX start() {
        return new GarbageTask(tile);
    }

}
