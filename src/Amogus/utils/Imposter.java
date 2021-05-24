package Amogus.utils;

import Amogus.Game;
import arc.graphics.Color;
import arc.math.Mathf;
import arc.struct.Seq;
import arc.util.Interval;
import mindustry.Vars;
import mindustry.content.Fx;
import mindustry.gen.Call;
import mindustry.gen.Player;

public class Imposter extends PlayerData {

    public static final float KILL_RANGE = Vars.tilesize * 2;
    public static final float KILL_TIMER = 15f;
    public static Seq<Imposter> ALL = new Seq<>();

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
        if (time < 0) {
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
        }
    }

    @Override
    public void clear() {
        super.clear();
        Game.ME.level.imposters--;
    }
    
    
}
