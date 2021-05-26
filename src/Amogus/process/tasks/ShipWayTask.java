package Amogus.process.tasks;

import Amogus.process.TaskX;
import Amogus.utils.Crewmate;
import Amogus.utils.Dot;
import Amogus.utils.M;
import arc.graphics.Color;
import arc.struct.Seq;
import mindustry.Vars;
import mindustry.content.Fx;
import mindustry.gen.Call;
import mindustry.world.Tile;

public class ShipWayTask extends TaskX {

    public Seq<Dot> ways;
    public int current = 0;
    public int time = 0;

    public ShipWayTask(Tile tile) {
        super(tile);
        ways = new Seq<>();
        ways.add(new Dot(tile.drawx() - Vars.tilesize * 3f, tile.drawy() - 1.5f * Vars.tilesize));
        ways.add(new Dot(tile.drawx() - Vars.tilesize * 1.5f, tile.drawy() + 1.4f * Vars.tilesize));
        ways.add(new Dot(tile.drawx() + Vars.tilesize * 0f, tile.drawy() - 1.6f * Vars.tilesize));
        ways.add(new Dot(tile.drawx() + Vars.tilesize * 1.5f, tile.drawy() + 1.2f * Vars.tilesize));
        ways.add(new Dot(tile.drawx() + Vars.tilesize * 3f, tile.drawy() - 1.2f * Vars.tilesize));
    }

    @Override
    public void update(Crewmate data) {
        if (time <= 0) {
            time = 30;
            for (Dot way : ways) {
                Call.effect(data.player.con, Fx.freezing, way.x, way.y, 0, Color.clear);
            }
            if (current < ways.size && ways.get(current).in(data.player.mouseX, data.player.mouseY, 12)) {
                current++;
            }
            ways.get(current);
            if (current > 0) {
                for (int i = 0; i < current; i++) {
                    if (current > ways.size - 1) return;
                    Dot way0 = ways.get(i);
                    Dot way1 = ways.get(i - 1);
                    M.line(way0.x, way0.y, way1.x, way1.y, (x, y) -> {
                        if (x % 10 <= 0) {
                            Call.effect(Fx.freezing, x, y, 0, Color.clear);
                        }
                    });
                }
            }
        } else {
            time -= 1;
        }
        if (current >= ways.size - 1) {
            onFinish(data);
        }
    }

    @Override
    public void onStart(Crewmate data) {
        resc(data);
    }

    @Override
    public TaskX start() {
        return new ShipWayTask(tile);
    }

}
