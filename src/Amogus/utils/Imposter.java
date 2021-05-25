package Amogus.utils;

import Amogus.Game;
import Amogus.MainX;
import Amogus.process.Trapdoor;
import arc.graphics.Color;
import arc.math.Mathf;
import arc.struct.Seq;
import arc.util.Interval;
import mindustry.Vars;
import mindustry.content.Fx;
import mindustry.gen.Call;
import mindustry.gen.Nulls;
import mindustry.gen.Player;
import mindustry.gen.Unit;

public class Imposter extends PlayerData {

    public static final float KILL_RANGE = Vars.tilesize * 2;
    public static final float KILL_TIMER = 15f;
    public static Seq<Imposter> ALL = new Seq<>();

    public Trapdoor in;
    public float statusTime = 0;
    
    float time = KILL_TIMER;
    Interval interval;

    public Imposter(Player player) {
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
        if (statusTime >= 0) {
            statusTime -= 1;
        }
        if (time < 0 && in == null) {
            for (Crewmate crewmate : Crewmate.ALL) {
                if (!crewmate.dead && player.unit() != null && player.unit().dst(crewmate.player) < KILL_RANGE && player.unit().isShooting) {
                    crewmate.kill();
                    crewmate.dead = true;
                    for (int i = 0; i < 15; i++) {
                        Call.effect(Fx.bubble, crewmate.player.x, crewmate.player.y, 0, Color.red);
                    }
                    time = KILL_TIMER;
                }
            }
        }
        if (interval.get(0, 60f)) {
            Call.setHudText(player.con, time < 0 ? "[crimson]KILL" : "[accent]" + Mathf.floor(time));
            time = time - 1;
            if (statusTime < 0 && in != null) {
                for (Trapdoor trap : in.connections) {
                    Call.effect(player.con, Fx.bubble, trap.tile.drawx(), trap.tile.drawy(), 0, Color.crimson);
                    if (player.unit().isShooting() && cx > trap.tile.drawx() - 12 && cx < trap.tile.drawx() + 12 && cy > trap.tile.drawy() - 12 && cy < trap.tile.drawy() + 12) {
                        Call.setRules(player.con, MainX.rules);
                        transfer(trap.tile.drawx(), trap.tile.drawy());
                        // blinking to show xd
                        player.unit().damage(0);
                        in = null;
                        statusTime = 120;
                    }
                }
            }
        }
    }

    @Override
    public void clear() {
        super.clear();
        Game.ME.level.imposters--;
    }

    public void transfer(float x, float y) {
        Unit u = player.unit();
        player.unit(Nulls.unit);
        u.set(x, y);
        player.unit(u);
    }
}
