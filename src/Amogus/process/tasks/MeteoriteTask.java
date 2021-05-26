package Amogus.process.tasks;

import Amogus.process.TaskX;
import Amogus.utils.Crewmate;
import Amogus.utils.Dot;
import arc.graphics.Color;
import arc.math.Mathf;
import arc.struct.Seq;
import mindustry.Vars;
import mindustry.content.Fx;
import mindustry.gen.Call;
import mindustry.world.Tile;

public class MeteoriteTask extends TaskX {

    public Seq<Dot> meteorites;

    public MeteoriteTask(Tile tile) {
        super(tile);
        meteorites = new Seq();
        for (int i = 0; i < Mathf.random(7, 15); i++) {
            meteorites.add(new Dot(tile.drawx() + Mathf.random(-Vars.tilesize * 3.5f, Vars.tilesize * 3.5f), tile.drawy() + Mathf.random(-Vars.tilesize * 3.5f, Vars.tilesize * 3.5f)));
        }
    }

    @Override
    public void update(Crewmate data) {
        if (data.player.unit().isShooting) {
            for (Dot meteorite : meteorites) {
                if (meteorite.in(data.cx, data.cy, Vars.tilesize)) {
                    Call.effect(data.player.con, Fx.explosion, meteorite.x, meteorite.y, 0, Color.clear);
                    meteorites.remove(meteorite);
                }
            }
        }
        if (meteorites.isEmpty()) {
            onFinish(data);
        }
    }

    @Override
    public void onStart(Crewmate data) {
        resc(data);
    }

    @Override
    public TaskX start() {
        return new MeteoriteTask(tile);
    }
}
