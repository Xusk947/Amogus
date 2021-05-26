package Amogus.process.tasks;

import Amogus.process.TaskX;
import Amogus.utils.Crewmate;
import Amogus.utils.Dot;
import Amogus.utils.M;
import arc.graphics.Color;
import arc.util.Log;
import mindustry.Vars;
import mindustry.content.Fx;
import mindustry.gen.Call;
import mindustry.world.Tile;

public class SwipeCardTask extends TaskX {

    public Dot from, to;
    public boolean clicked = true;
    public boolean waitPop = false;
    public int timer = 50;

    public SwipeCardTask(Tile tile) {
        super(tile);
        from = new Dot(tile.drawx() - Vars.tilesize * 2.5f, tile.drawy());
        to = new Dot(tile.drawx() + Vars.tilesize * 2.5f, tile.drawy());
    }

    @Override
    public void update(Crewmate data) {
        if (data.player.unit().isShooting) {
            clicked = true;
        } else {
            clicked = false;
        }
        if (clicked) {
            timer--;
        } else {
            timer = 50;
            waitPop = false;
        }
        if (timer % 5 <= 0) {
            Call.effect(data.player.con, Fx.pointHit, from.x, from.y, 0, Color.lime);
            Call.effect(data.player.con, Fx.pointHit, to.x, to.y, 0, Color.crimson);
            if (clicked) {
                M.line(data.cx, data.cy, data.player.mouseX, data.player.mouseY, (x, y) -> {
                    if (x % 5 <= 0) {
                        Call.effect(data.player.con, Fx.pointHit, x, y, 0, Color.gray);
                    }
                });
                
                if (!waitPop && from.in(data.cx, data.cy, 0.8f) && to.in(data.player.mouseX, data.player.mouseY, 0.8f)) {
                    if (timer > 20 && timer < 40) {
                        onFinish(data);
                    } else if (timer < 20) {
                        resc(data);
                        waitPop = true;
                        Call.label("[red]To slow!", 1, tile.drawx(), tile.drawy());
                    } else {
                        resc(data);
                        waitPop = true;
                        Call.label("[sky]To fast!", 1, tile.drawx(), tile.drawy());
                    }
                }
            }
        }

    }

    @Override
    public void onStart(Crewmate data) {
        resc(data);
    }

    @Override
    public TaskX start() {
        return new SwipeCardTask(tile);
    }

}
