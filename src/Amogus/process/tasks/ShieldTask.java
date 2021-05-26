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

public class ShieldTask extends TaskX {

    Seq<Cell> cells;
    int time = 0;

    public ShieldTask(Tile tile) {
        super(tile);
        cells = new Seq<>();
        cells.add(new Cell(new Dot(-Vars.tilesize + tile.drawx(), Vars.tilesize * 1.5f + tile.drawy())));
        cells.add(new Cell(new Dot(Vars.tilesize + tile.drawx(), Vars.tilesize * 1.5f + tile.drawy())));
        cells.add(new Cell(new Dot(-Vars.tilesize + tile.drawx(), -Vars.tilesize * 1.5f + tile.drawy())));
        cells.add(new Cell(new Dot(Vars.tilesize + tile.drawx(), -Vars.tilesize * 1.5f + tile.drawy())));
        cells.add(new Cell(new Dot(-Vars.tilesize * 1.5f + tile.drawx(), tile.drawy())));
        cells.add(new Cell(new Dot(Vars.tilesize * 1.5f + tile.drawx(), tile.drawy())));
        cells.add(new Cell(new Dot(tile.drawx(), tile.drawy())));

        for (int i = 0; i < Mathf.random(1, 3); i++) {
            cells.shuffle();
            cells.get(0).enabled = false;
        }
    }

    @Override
    public void update(Crewmate data) {
        if (time <= 0) {
            time = 30;
            for (Cell cell : cells) {
                if (!cell.enabled && cell.p.in(data.cx, data.cy, 0.75f)) {
                    cell.enabled = true;
                }
                Call.effect(data.player.con, Fx.pointHit, cell.p.x, cell.p.y, 0, cell.enabled ? Color.lime : Color.crimson);
            }
        } else {
            time -= 1;
        }
        if (cells.count(c -> !c.enabled) <= 0) {
            onFinish(data);
        }
    }

    @Override
    public void onStart(Crewmate data) {
        resc(data);
    }

    @Override
    public TaskX start() {
        return new ShieldTask(tile);
    }

    private class Cell {

        public Dot p;
        public boolean enabled = true;

        public Cell(Dot p) {
            this.p = p;
        }
    }
}
