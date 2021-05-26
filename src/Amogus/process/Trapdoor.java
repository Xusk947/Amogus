package Amogus.process;

import Amogus.MainX;
import Amogus.utils.Imposter;
import arc.graphics.Color;
import arc.struct.Seq;
import arc.util.Interval;
import mindustry.content.Fx;
import mindustry.gen.Call;
import mindustry.world.Tile;

public class Trapdoor {
    
    public Seq<Trapdoor> connections;
    public Tile tile;
    public Interval interval;
    
    public Trapdoor(Tile tile) {
        this.tile = tile;
        this.connections = new Seq<>();
        interval = new Interval(1);
    }
    
    public void connect(Trapdoor trap) {
        connections.add(trap);
        trap.connections.add(this);
    }
    
    public void update(Imposter imp) {
        if (!imp.player.unit().isNull() && imp.player.tileX() >= tile.x - 1 && imp.player.tileX() <= tile.x + 1 && imp.player.tileY() >= tile.y - 1 && imp.player.tileY() <= tile.y + 1) {
            if (interval.get(0, 60f)) {
                Call.effect(imp.player.con, Fx.bubble, tile.drawx(), tile.drawy(), 0, Color.crimson);
            }
            if (imp.player.unit().isShooting() && imp.player.unit().aimX > tile.drawx() - 12 && imp.player.unit().aimX < tile.drawx() + 12 && imp.player.unit().aimY > tile.drawy() - 12 && imp.player.unit().aimY < tile.drawy() + 12) {
                imp.in = this;
                imp.transfer(4, 4);
                Call.setRules(imp.player.con, MainX.lobby);
            }
        }
    }
}
