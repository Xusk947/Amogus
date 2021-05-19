package Amogus;

import Amogus.utils.PlayerData;
import arc.Events;
import arc.graphics.Color;
import arc.struct.IntMap;
import arc.util.CommandHandler;
import arc.util.Log;
import mindustry.Vars;
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

        Events.on(EventType.ServerLoadEvent.class, e -> {
            loadMaps();
            game.lobby.go();
            Vars.netServer.openServer();
        });

        Events.run(EventType.Trigger.update, () -> {
            game.update();
        });

        Vars.netServer.admins.addChatFilter((player, message) -> null);

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
                        Call.sendMessage("[white] vote to: " + NAMES.get(data.player.id) + " [#" + Team.get(data.id).color.toString() + "]" + data.id, NAMES.get(e.player.id) + " [#" + Team.get(e.player.team().id).color.toString() + "]" + e.player.team().id, Nulls.player);
                    } else if (!PlayerData.ALL.find(d -> d.player.id == e.player.id).dead) {
                        Call.sendMessage(e.player.con, "Can't find player with: " + s[1], "Amogus", Nulls.player);
                    } else {
                        Call.sendMessage(e.player.con, "You [crimson]DED: " + s[1], "Amogus", Nulls.player);

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
