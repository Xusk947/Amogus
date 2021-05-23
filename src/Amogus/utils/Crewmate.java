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

public class Crewmate extends PlayerData {

    public static Seq<Crewmate> ALL = new Seq<>();

    public Interval interval;
    public TaskX currentTask;
    
    public Crewmate(Player player) {
        super(player);
        interval = new Interval(1);
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
                if (player.tileX() > task.tile.x - 5 && player.tileY() > task.tile.y - 5 && player.tileX() <= task.tile.x + 5 && player.tileY() <= task.tile.y + 5) {
                    Call.effect(player.con, Fx.bubble, task.tile.drawx(), task.tile.drawy(), 0, Color.yellow);
                    break;
                }
            }
        } else if (currentTask != null) {
            currentTask.update(this);
            if (!dead || currentTask.tile.dst(player) > Vars.tilesize * 3) currentTask = null;
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
}
