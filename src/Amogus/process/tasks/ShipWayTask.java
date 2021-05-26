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
            time = 15;
            for (Dot way : ways) {
                Call.effect(data.player.con, Fx.freezing, way.x, way.y, 0, Color.clear);
                int index = ways.indexOf(way);
                if (index == current) {
                    M.line(way.x, way.y, data.player.mouseX, data.player.mouseY, (x, y) -> {
                        if (x % 7 <= 0) {
                            Call.effect(data.player.con, Fx.pointHit, x, y, 0, Color.gray);
                        }
                    });
                    if (current < ways.size - 1 && ways.get(index + 1).in(data.player.mouseX, data.player.mouseY, 0.8f)) {
                        current++;
                    }
                }
                if (index < current) {
                    Dot way1 = ways.get(index + 1);
                    M.line(way.x, way.y, way1.x, way1.y, (x, y) -> {
                        if (x % 5 <= 0) {
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
