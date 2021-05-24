package Amogus.utils;

import Amogus.Game;
import Amogus.MainX;
import Amogus.process.TaskX;
import arc.graphics.Color;
import arc.struct.Seq;
import arc.util.Interval;
import mindustry.Vars;
import mindustry.content.Fx;
import mindustry.gen.Call;
import mindustry.gen.Nulls;
import mindustry.gen.Player;
import mindustry.gen.Unit;
import mindustry.world.Tile;

public class Crewmate extends PlayerData {

    public static Seq<Crewmate> ALL = new Seq<>();

    public Interval interval;
    public TaskX currentTask;
    public Seq<Tile> finished;

    public Crewmate(Player player) {
        super(player);
        interval = new Interval(1);
        finished = new Seq<>();
        ALL.add(this);
    }

    @Override
    public void remove() {
        super.remove();
        ALL.remove(this);
    }

    @Override
    public void update() {
        super.update();
        if (interval.get(60f) && currentTask == null) {
            for (TaskX task : Game.ME.level.tasks) {
                if (finished.contains(t -> t == task.tile)) continue;
                if (in(task, 5)) {
                    Call.effect(player.con, Fx.dooropen, task.tile.drawx(), task.tile.drawy(), 0, Color.yellow);
                    break;
                }
            }
        } else if (currentTask != null) {
            currentTask.update(this);
            if (currentTask != null && !in(currentTask, 5)) {
                currentTask = null;
            }
        }
    }

    public void kill() {
        if (player.unit() != null) {
            new DeadBody(player.x, player.y, this);
            Unit unit = player.unit();
            player.clearUnit();
            unit.set(0, 0);
            unit.kill();
            player.unit(Nulls.unit);
            Call.setRules(player.con, MainX.lobby);
        }
    }

    public boolean in(TaskX task, int dst) {
        return player.unit().tileX() > task.tile.x - dst && player.unit().tileY() > task.tile.y - dst && player.unit().tileX() <= task.tile.x + dst && player.unit().tileY() <= task.tile.y + dst;
    }
}
