package Amogus.process.tasks;

import Amogus.process.TaskX;
import Amogus.utils.Crewmate;
import Amogus.utils.Dot;
import Amogus.utils.M;
import arc.graphics.Color;
import mindustry.Vars;
import mindustry.content.Fx;
import mindustry.gen.Call;
import mindustry.world.Tile;

public class SwipeCardTask extends TaskX {

    public Dot from, to;
    public boolean clicked = true;
    public boolean waitPop = false;
    public int timer = 40;

    public SwipeCardTask(Tile tile) {
        super(tile);
        from = new Dot(tile.drawx() - Vars.tilesize * 2.5f, tile.drawy());
        to = new Dot(tile.drawx() + Vars.tilesize * 2.5f, tile.drawy());
    }

    @Override
    public void update(Crewmate data) {
        if (data.player.shooting) {
            clicked = true;
            if (timer % 5 <= 0) {
                Call.effect(data.player.con, Fx.pointHit, from.x, from.y, 0, Color.lime);
                Call.effect(data.player.con, Fx.pointHit, to.x, to.y, 0, Color.crimson);
                M.line(data.cx, data.cy, data.player.mouseX, data.player.mouseY, (x, y) -> {
                    if (x % 5 <= 0) {
                        Call.effect(data.player.con, Fx.pointHit, x, y, 0, Color.gray);
                    }
                });
            }
            if (!waitPop && from.in(data.cx, data.cy, 12) && to.in(data.player.mouseX, data.player.mouseY, 12)) {
                if (timer > 10 && timer < 20) {
                    onFinish(data);
                } else if (timer <= 10) {
                    resc(data);
                    waitPop = true;
                    Call.label(data.player.con, "[red]to Slow", 1, tile.drawx(), tile.drawy());
                } else {
                    resc(data);
                    waitPop = true;
                    Call.label(data.player.con, "[green]to Fast", 1, tile.drawx(), tile.drawy());
                }
            }
        } else {
            clicked = false;
            waitPop = false;
            timer = 40;
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
