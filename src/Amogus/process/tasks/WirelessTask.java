package Amogus.process.tasks;

import Amogus.process.TaskX;
import Amogus.utils.Crewmate;
import Amogus.utils.Dot;
import Amogus.utils.M;
import arc.graphics.Color;
import arc.struct.IntSeq;
import arc.util.Log;
import arc.util.Time;
import mindustry.Vars;
import mindustry.content.Fx;
import mindustry.gen.Call;
import mindustry.world.Tile;

public class WirelessTask extends TaskX {
    
    public static int ID = 0;
    int id = 0;
    float timer = 0;

    Con[] connections;

    public WirelessTask(Tile tile) {
        super(tile);
        id = ID;
        ID++;
        this.connections = new Con[4];

        IntSeq se = new IntSeq(new int[]{0, 1, 2, 3});
        se.shuffle();
        
        for (int i = 0; i < connections.length; i++) {
            float nn = se.get(0) * Vars.tilesize * 1.3f;
            se.removeIndex(0);
            connections[i] = new Con(new Dot(tile.drawx() - Vars.tilesize * 3.5f, tile.drawy() + Vars.tilesize * 1 + i * Vars.tilesize * 1.3f), new Dot(tile.drawx() + Vars.tilesize * 3.5f, tile.drawy() + Vars.tilesize * 1 + nn), i);
        }
    }

    @Override
    public void update(Crewmate data) {
        if (timer < 0) {
            Log.info(id);
            M.line(data.cx, data.cy, data.player.mouseX, data.player.mouseY, (x, y) -> {
                if (x % 10 <= 0) {
                    Call.effect(data.player.con, Fx.pointHit, x, y, 0, Color.gray);
                }
            });
            Call.effect(data.player.con, Fx.heal, data.cx, data.cy, 0, Color.gray);
            timer = 30;
            boolean canFinish = true;
            for (Con connection : connections) {
                if (!connection.finished) {
                    canFinish = false;
                    Call.effect(data.player.con, Fx.pointHit, connection.from.x, connection.from.y, 0, connection.color);
                    Call.effect(data.player.con, Fx.pointHit, connection.out.x, connection.out.y, 0, connection.color);
                    if (connection.from.in(data.cx, data.cy, 1)) {
                        if (connection.out.in(data.player.mouseX, data.player.mouseY, 1)) {
                            connection.finished = true;
                        }
                    }
                } else if (connection.finished) {
                    M.line(connection.from.x, connection.from.y, connection.out.x, connection.out.y, (x, y) -> {
                        if (x % 15 <= 0) {
                            Call.effect(data.player.con, Fx.pointHit, x, y, 0, connection.color);
                        }
                    });
                }
            }
            if (canFinish) {
                onFinish(data);
            }
        } else {
            timer -= Time.delta;
        }
    }

    @Override
    public void onStart(Crewmate data) {
        data.ctime = 0;
        data.cx = 0;
        data.cy = 0;
    }

    @Override
    public TaskX start() {
        return new WirelessTask(tile);
    }

    private class Con {

        public Dot from, out;
        public boolean finished = false;
        public Color color;

        public Con(Dot from, Dot out, int index) {
            this.from = from;
            this.out = out;

            switch (index) {
                case 0:
                    color = Color.red;
                    break;
                case 1:
                    color = Color.yellow;
                    break;
                case 2:
                    color = Color.green;
                    break;
                case 3:
                    color = Color.blue;
                    break;
            }
        }
    }
}
