package Amogus;

import Amogus.utils.PlayerData;
import arc.Events;
import arc.graphics.Color;
import arc.struct.IntMap;
import arc.util.CommandHandler;
import mindustry.Vars;
import mindustry.content.UnitTypes;
import mindustry.game.EventType;
import mindustry.game.Rules;
import mindustry.game.Team;
import mindustry.gen.Call;
import mindustry.gen.Groups;
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
            if (NAMES.containsKey(e.player.id)) {
                String n = "[white]" + NAMES.get(e.player.id);
                if (Game.ME.state == Game.State.Game) {
                    if (PlayerData.ALL.contains(d -> d.player.id == e.player.id)) {
                        if (Game.ME.level.discussion) {
                            Call.sendMessage(e.message, n, e.player);
                        } else if (PlayerData.ALL.find(d -> d.player.id == e.player.id).dead) {
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
        handler.<Player>register("info", "Info for [red]Amogus", (args, player) -> {
            player.sendMessage("");
        });
        handler.<Player>register("inforu", "Инфо по [red]Амогус", (args, player) -> {
            player.sendMessage("");
        });
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
