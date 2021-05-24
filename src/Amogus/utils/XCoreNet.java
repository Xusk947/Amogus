package Amogus.utils;

import arc.Core;
import arc.math.geom.Vec2;
import arc.util.Interval;
import mindustry.core.NetServer;
import arc.math.geom.Rect;
import arc.util.Log;
import arc.util.Time;
import arc.util.io.ReusableByteOutStream;
import arc.util.io.Writes;
import java.io.DataOutputStream;
import java.io.IOException;
import static mindustry.Vars.*;
import mindustry.game.Teams;
import mindustry.gen.Building;
import mindustry.gen.Call;
import mindustry.gen.Groups;
import mindustry.gen.Player;
import mindustry.gen.Posc;
import mindustry.gen.Syncc;
import mindustry.world.blocks.storage.CoreBlock;

public class XCoreNet extends NetServer {

    private static final int maxSnapshotSize = 800, timerBlockSync = 0, serverSyncTime = 50;
    private static final float blockSyncTime = 60 * 6;
    private static boolean closing;
    private static final Vec2 vector = new Vec2();
    private static final Rect viewport = new Rect();
    private Interval timer = new Interval();
    /**
     * Stream for writing player sync data to.
     */
    private ReusableByteOutStream syncStream = new ReusableByteOutStream();
    /**
     * Data stream for writing player sync data to.
     */
    private DataOutputStream dataStream = new DataOutputStream(syncStream);

    public XCoreNet() {
        super();
        Log.info("XCoreNet | Loaded");
    }

    @Override
    public void update() {
        if (!headless && !closing && net.server() && state.isMenu()) {
            closing = true;
            ui.loadfrag.show("@server.closing");
            Time.runTask(5f, () -> {
                net.closeServer();
                ui.loadfrag.hide();
                closing = false;
            });
        }

        if (state.isGame() && net.server()) {
            if (state.rules.pvp) {
                state.serverPaused = isWaitingForPlayers();
            }

            sync();
        }
    }

    @Override
    public void writeEntitySnapshot(Player player) throws IOException {
        syncStream.reset();
        int sum = state.teams.present.sum(t -> t.cores.size);

        dataStream.writeInt(sum);

        for (Teams.TeamData data : state.teams.present) {
            for (CoreBlock.CoreBuild entity : data.cores) {
                dataStream.writeInt(entity.pos());
                entity.items.write(Writes.get(dataStream));
            }
        }

        dataStream.close();
        byte[] stateBytes = syncStream.toByteArray();

        //write basic state data.
        Call.stateSnapshot(player.con, state.wavetime, state.wave, state.enemies, state.serverPaused, state.gameOver, universe.seconds(), (short) stateBytes.length, net.compressSnapshot(stateBytes));

        viewport.setSize(player.con.viewWidth, player.con.viewHeight).setCenter(player.con.viewX, player.con.viewY);

        syncStream.reset();

        int sent = 0;

        for (Syncc entity : Groups.sync) {
            //write all entities now
            if (!player.unit().isNull() && entity instanceof Posc) {
                if (player.dst((Posc) entity) > tilesize * 8) {
                    continue;
                } else {
                    if (M.ray(((Posc) entity).x(), ((Posc) entity).y(), player.unit().x, player.unit().y)) {
                        continue;
                    }
                }
            }
            dataStream.writeInt(entity.id()); //write id
            dataStream.writeByte(entity.classId()); //write type ID
            entity.writeSync(Writes.get(dataStream)); //write entity

            sent++;

            if (syncStream.size() > maxSnapshotSize) {
                dataStream.close();
                byte[] syncBytes = syncStream.toByteArray();
                Call.entitySnapshot(player.con, (short) sent, (short) syncBytes.length, net.compressSnapshot(syncBytes));
                sent = 0;
                syncStream.reset();
            }
        }

        if (sent > 0) {
            dataStream.close();

            byte[] syncBytes = syncStream.toByteArray();
            Call.entitySnapshot(player.con, (short) sent, (short) syncBytes.length, net.compressSnapshot(syncBytes));
        }

    }

    @Override
    public void writeBlockSnapshots() throws IOException {
        syncStream.reset();

        short sent = 0;
        for (Building entity : Groups.build) {
            if (!entity.block.sync) {
                continue;
            }
            sent++;

            dataStream.writeInt(entity.pos());
            dataStream.writeShort(entity.block.id);
            entity.writeAll(Writes.get(dataStream));

            if (syncStream.size() > maxSnapshotSize) {
                dataStream.close();
                byte[] stateBytes = syncStream.toByteArray();
                Call.blockSnapshot(sent, (short) stateBytes.length, net.compressSnapshot(stateBytes));
                sent = 0;
                syncStream.reset();
            }
        }

        if (sent > 0) {
            dataStream.close();
            byte[] stateBytes = syncStream.toByteArray();
            Call.blockSnapshot(sent, (short) stateBytes.length, net.compressSnapshot(stateBytes));
        }
    }

    void sync() {
        try {
            Groups.player.each(p -> !p.isLocal(), player -> {
                if (player.con == null || !player.con.isConnected()) {
                    onDisconnect(player, "disappeared");
                    return;
                }
                if (player.con().lastReceivedClientSnapshot % serverSyncTime < 1 || !player.con.hasConnected) {
                    return;
                }
                try {
                    writeEntitySnapshot(player);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });

            if (Groups.player.size() > 0 && Core.settings.getBool("blocksync") && timer.get(timerBlockSync, blockSyncTime)) {
                writeBlockSnapshots();
            }

        } catch (IOException e) {
            Log.err(e);
        }
    }

}
