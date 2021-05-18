package Amogus.utils;

import Amogus.Game;
import arc.math.Mathf;
import arc.struct.Seq;
import arc.util.Interval;
import arc.util.Time;
import mindustry.Vars;
import mindustry.gen.Call;
import mindustry.gen.Player;

public abstract class PlayerData {

    public static Seq<PlayerData> ALL = new Seq<>();
    public static float VISIBLE_RANGE = (Vars.tilesize * 3);

    public final String name;
    public Player player;
    public boolean dead = false;
    
    Interval bodyInterval;

    public PlayerData(Player player) {
        this.player = player;
        this.name = player.name;
        this.player.name("[black][]");
        bodyInterval = new Interval(1);
        ALL.add(this);
    }

    public void remove() {
        ALL.remove(this);
    }

    public void update() {
        if (DeadBody.ALL.size > 0) {
            for (DeadBody body : DeadBody.ALL) {
                if (body.actived && !dead && player.unit() != null && player.unit().health > 0 && player.unit().dst(body.x, body.y) <= VISIBLE_RANGE) {
                    if (bodyInterval.get(0, 60f)) {
                        Call.label(player.con, "[red]REPORT", 1f, body.x, body.y);
                        float mx = player.mouseX, my = player.mouseY;
                        if (player.unit().isShooting && Mathf.dst(mx, my, body.x, body.y) <= VISIBLE_RANGE) {
                            Game.ME.level.reportBody(this);
                            body.remove();
                        }
                    }
                }
            }
        }
    }
;
}
