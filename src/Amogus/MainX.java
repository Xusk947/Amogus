package Amogus;

import Amogus.process.TaskX;
import Amogus.utils.Crewmate;
import Amogus.utils.PlayerData;
import Amogus.utils.XCoreNet;
import arc.Core;
import arc.Events;
import arc.graphics.Color;
import arc.struct.IntMap;
import arc.util.CommandHandler;
import arc.util.Log;
import mindustry.Vars;
import mindustry.content.Blocks;
import mindustry.content.UnitTypes;
import mindustry.game.EventType;
import mindustry.game.Rules;
import mindustry.game.Team;
import mindustry.gen.Call;
import mindustry.gen.Groups;
import mindustry.gen.Nulls;
import mindustry.gen.Player;
import mindustry.mod.Plugin;

/**
 *
 * @author Xusk
 */
public class MainX extends Plugin {

    public static IntMap<String> NAMES = new IntMap<>();
    public static MainX ME;
    public static Rules rules;
    public static Rules lobby;
    public Game game;

    @Override
    public void init() {
        ME = this;
        game = new Game();
        rules = new Rules();
        rules.enemyLights = false;
        rules.lighting = true;
        rules.ambientLight = new Color(0, 0, 0, 255);
        rules.waves = false;
        rules.fire = false;
        rules.unitDamageMultiplier = 0;
        rules.blockDamageMultiplier = 0;
        rules.unitAmmo = true;

        lobby = new Rules();
        lobby.enemyLights = true;
        lobby.lighting = false;
        lobby.unitDamageMultiplier = 0f;
        lobby.blockDamageMultiplier = 0f;
        lobby.reactorExplosions = false;
        lobby.canGameOver = false;

        Events.on(EventType.ServerLoadEvent.class, e -> {
            Core.app.removeListener(Vars.netServer);
            Vars.netServer = new XCoreNet();
            Vars.netServer.admins.addChatFilter((player, message) -> null);
            Core.app.addListener(Vars.netServer);
            loadMaps();
            game.lobby.go();
            Vars.netServer.openServer();
        });

        Events.run(EventType.Trigger.update, () -> {
            game.update();
        });

        Events.on(EventType.PlayerChatEvent.class, e -> {
            // Vote session because ClientComandHandler is Broken  lol
            if (e.message.startsWith("vote") && Game.ME.level.discussion && PlayerData.ALL.contains(d -> d.player.id == e.player.id) && !PlayerData.ALL.find(d -> d.player.id == e.player.id).hasVoted) {
                String[] s = e.message.split(" ");
                if (s.length > 1) {
                    PlayerData data = PlayerData.ALL.find(d -> d.id == Integer.valueOf(s[1]));
                    if (data != null && !data.dead && !PlayerData.ALL.find(d -> d.player.id == e.player.id).dead) {
                        data.votes++;
                        Log.info(data.votes);
                        PlayerData.ALL.find(d -> d.player.id == e.player.id).hasVoted = true;
                        Game.ME.level.voted++;
                        Call.sendMessage("[white] vote to: " + NAMES.get(data.player.id) + " [#" + Team.get(data.id).color.toString() + "]" + data.id, NAMES.get(e.player.id) + " [#" + Team.get(e.player.team().id).color.toString() + "]" + e.player.team().id, Nulls.player);
                    } else if (!PlayerData.ALL.find(d -> d.player.id == e.player.id).dead) {
                        Call.sendMessage(e.player.con, "Can't find player with: " + s[1], "Amogus", Nulls.player);
                    } else {
                        Call.sendMessage(e.player.con, "You [crimson]DED", "Amogus", Nulls.player);

                    }
                }
            } else if (e.message.startsWith("skip")) {

            } else if (NAMES.containsKey(e.player.id)) {
                String n = "[white]" + NAMES.get(e.player.id);
                if (Game.ME.state == Game.State.Game) {
                    if (PlayerData.ALL.contains(d -> d.player.id == e.player.id)) {
                        PlayerData data = PlayerData.ALL.find(d -> d.player.id == e.player.id);
                        if (Game.ME.level.discussion && !data.dead) {
                            Call.sendMessage(e.message, n + " [#" + Team.get(data.id).color.toString() + "]" + data.id, e.player);
                        } else if (data.dead) {
                            Call.sendMessage(e.player.con(), e.message, "[gray][S] " + n, e.player);
                        }
                    } else {
                        for (Player player : Groups.player) {
                            if (!PlayerData.ALL.contains(d -> player.id == d.player.id)) {
                                Call.sendMessage(player.con(), e.message, "[gray][S] " + n, player);
                            }
                        }
                    }
                } else {
                    Call.sendMessage(e.message, n, e.player);
                }
            }
        });

        Events.on(EventType.PlayerJoin.class, e -> {
            NAMES.put(e.player.id, e.player.name);
            if (Game.ME.state == Game.State.Game) {
                e.player.team(Team.blue);
                Call.setRules(e.player.con, lobby);
            }
        });

        Events.on(EventType.PlayerLeave.class, e -> {
            NAMES.remove(e.player.id);
            if (e.player.unit() != null && !e.player.unit().spawnedByCore) {
                e.player.unit().kill();
            }
        });

        Events.on(EventType.WorldLoadEvent.class, e -> {
            UnitTypes.crawler.weapons.clear();
        });

        Events.on(EventType.ConfigEvent.class, e -> {
            if (e.player == null) return;
            if (Game.ME.state == Game.State.Game && Crewmate.ALL.contains(c -> c.player.id() == e.player.id()) && e.tile != null && e.tile.block == Blocks.switchBlock) {
                if (Game.ME.level.tasks.contains(t -> t.tile.build.equals(e.tile))) {
                    Log.info(e.tile.tileX() + " : " + e.tile.tileY());
                    TaskX task = Game.ME.level.tasks.find(t -> t.tile.build.equals(e.tile));
                    int tx = e.player.unit().tileX(), ty = e.player.unit().tileY();
                    if (tx >= e.tile.tileX() - 2 && ty >= e.tile.tileY() - 2 && tx <= e.tile.tileX() + 2 && ty <= e.tile.tileY() + 2) {
                        Call.label(e.player.con, "[accent]Task started", 1, task.tile.drawx(), task.tile.drawy());
                        Crewmate crew = Crewmate.ALL.find(c -> c.player.id == e.player.id);
                        if (!(crew.finished.contains(f -> f.build == e.tile))) {
                            crew.currentTask = task.start();
                        }
                    }
                }
            }
        });
    }

    @Override
    public void registerClientCommands(CommandHandler handler) {
    }

    public void loadMaps() {
//        try {
//            Streams.copy(
//                    Objects.requireNonNull(MainX.class.getClassLoader().getResourceAsStream("ship.msav")),
//                    Vars.customMapDirectory.child("ship.msav").write(false));
//        } catch (IOException e) {
//            Log.err(e);
//        }
    }

}
