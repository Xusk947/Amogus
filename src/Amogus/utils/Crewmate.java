package Amogus.utils;

import Amogus.MainX;
import arc.struct.Seq;
import mindustry.gen.Call;
import mindustry.gen.Nulls;
import mindustry.gen.Player;
import mindustry.gen.Unit;

public class Crewmate extends PlayerData {

    public static Seq<Crewmate> ALL = new Seq<>();

    public Crewmate(Player player) {
        super(player);
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
