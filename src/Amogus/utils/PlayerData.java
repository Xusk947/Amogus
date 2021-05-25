package Amogus.utils;

import Amogus.Game;
import Amogus.MainX;
import arc.graphics.Color;
import arc.math.Mathf;
import arc.math.geom.Ray;
import arc.struct.Seq;
import arc.util.Interval;
import mindustry.Vars;
import mindustry.content.Fx;
import mindustry.core.World;
import mindustry.gen.Call;
import mindustry.gen.Nulls;
import mindustry.gen.Player;
import mindustry.gen.Unit;

public abstract class PlayerData {

    public static Seq<PlayerData> ALL = new Seq<>();
    public static float VISIBLE_RANGE = (Vars.tilesize * 3);

    public final String name;
    public Player player;
    public Unit unit;
    public boolean dead = false;
    public boolean hasVoted = false;
    public float cx, cy, ctime; // click (x y)
    public int id;
    public int votes = 0;

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
        // like holding xd for some tasks
        if (player.unit() != null) {
            if (player.unit().isShooting()) {
                if (ctime <= 0) {
                    cx = player.unit().aimX;
                    cy = player.unit().aimY;
                    ctime = 30;
                } else {
                    ctime = 30;
                }
            } else {
                ctime -= 1;
            }
        } else if (unit != null) {
            player.unit(unit);
        }
    }

    public void clear() {
        if (player.unit() != null) {
            Unit unit = player.unit();
            Call.effect(Fx.impactcloud, unit.x, unit.y, 0, Color.clear);
            Call.label(MainX.NAMES.get(player.id) + "[white] : was " + ((this instanceof Imposter) ? "[red]Imposter" : "[accent]Crewmate"), 3, unit.x, unit.y);
            player.clearUnit();
            unit.set(0, 0);
            unit.kill();
            player.unit(Nulls.unit);
            Call.setRules(player.con, MainX.lobby);
        }
    }
}
