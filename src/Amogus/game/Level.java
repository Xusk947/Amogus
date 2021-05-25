package Amogus.game;

import Amogus.Game;
import Amogus.MainX;
import Amogus.process.Door;
import Amogus.process.TaskX;
import Amogus.process.Trapdoor;
import Amogus.utils.Crewmate;
import Amogus.utils.DeadBody;
import Amogus.utils.Imposter;
import Amogus.utils.PlayerData;
import Amogus.utils.Speed;
import Amogus.utils.StateX;
import arc.struct.Seq;
import arc.util.Interval;
import arc.util.Log;
import arc.util.Timer;
import mindustry.Vars;
import mindustry.content.Blocks;
import mindustry.content.UnitTypes;
import mindustry.game.Team;
import mindustry.gen.Call;
import mindustry.gen.Groups;
import mindustry.gen.Nulls;
import mindustry.gen.Player;
import mindustry.gen.Unit;
import mindustry.world.Tile;
import mindustry.world.blocks.environment.Floor;

public class Level implements StateX {

    public final int DISCUSSION_TIME = 30;
    public int TEAM_ID = 65;

    public String name;

    public Speed speed = Speed.NORMAL;

    public boolean gameStarted = false, ended = false;
    public boolean discussion = false;

    public int time = DISCUSSION_TIME;

    public int needImposters = 1;
    public int imposters = 0;
    public int voted = 0;

    public Seq<PlayerData> datas;
    public Seq<Door> doors;
    public Seq<TaskX> tasks;
    public Seq<Trapdoor> traps;

    public int skip = 0;

    Interval interval;

    public Level() {
        interval = new Interval(2);
    }

    @Override
    public void go() {
        Game.ME.state = Game.State.Game;

        DeadBody.ALL = new Seq<>();
        Imposter.ALL = new Seq<>();
        Crewmate.ALL = new Seq<>();
        PlayerData.ALL = new Seq<>();

        datas = new Seq<>();
        doors = new Seq<>();
        tasks = new Seq<>();
        traps = new Seq<>();

        discussion = false;
        gameStarted = false;
        ended = false;

        time = DISCUSSION_TIME;
        imposters = 0;

        Seq<Player> players = new Seq<>();
        Groups.player.copy(players);
        players.shuffle();

        Vars.logic.reset();

        Call.worldDataBegin();

        Vars.world.loadMap(Vars.maps.byName(name));
        genTiles();

        Vars.state.rules = MainX.rules.copy();

        Vars.logic.play();

        for (Player player : players) {
            Vars.netServer.sendWorldData(player);

            if (imposters < needImposters) {
                datas.add(new Imposter(player));
                imposters++;
            } else {
                datas.add(new Crewmate(player));
            }
            player.team(Team.get(TEAM_ID));
            TEAM_ID++;

            if (TEAM_ID > 200) {
                TEAM_ID = 65;
            }
        }

        Timer.schedule(() -> {
            gameStarted = true;
            onStart();
        }, 1);
    }

    @Override
    public void update() {
        if (gameStarted && !ended) {
            for (PlayerData data : datas) {
                if (data.player.con != null && !discussion) {
                    cons(data);
                } else if (data.player.con == null) {
                    datas.remove(data);
                }
                if (discussion) {
                    if (interval.get(0, 60f)) {
                        time = time - 1;
                        Call.setHudText("[accent][ DISCUSSION " + Math.floor(time) + " ]");
                        if (time < 1) {
                            endDiscussion();
                        } else if (voted == datas.size) {
                            endDiscussion();
                        }
                    }
                }
            }
//            if (imposters >= Crewmate.ALL.size) {
//                endGame(true);
//            } else if (imposters <= 0) {
//                endGame(false);
//            }
        }
    }

    public void cons(PlayerData data) {
        data.update();
        if (data instanceof Imposter) {
            imposterCons((Imposter) data);
        }
    }

    public void imposterCons(Imposter imposter) {
        for (Door door : doors) {
            door.update(imposter);
        }
        for (Trapdoor trap : traps) {
            trap.update(imposter);
        }
    }

    public void onStart() {
        for (PlayerData data : datas) {
            if (Team.sharded.core() != null) {
                Unit unit = Nulls.unit;
                if (speed == Speed.FAST) {
                    unit = UnitTypes.dagger.spawn(Team.sharded, Team.sharded.core().x, Team.sharded.core().y + Vars.tilesize * 4);
                    unit.type = UnitTypes.flare;
                } else if (speed == Speed.NORMAL) {
                    unit = UnitTypes.crawler.spawn(Team.sharded, Team.sharded.core().x, Team.sharded.core().y + Vars.tilesize * 4);
                }
                unit.ammo = 0;
                data.player.unit(unit);
                data.unit = unit;
                data.id = data.player.team().id;
            }
        }
    }

    public void reportBody(PlayerData reporter) {
        Call.infoMessage(MainX.NAMES.get(reporter.player.id) + " [accent]report the body!");
        for (PlayerData data : PlayerData.ALL) {
            Unit unit = data.player.unit();
            data.player.unit(Nulls.unit);
            unit.set(Team.sharded.core().x, Team.sharded.core().y + Vars.tilesize * 4);
            data.player.unit(unit);

            data.player.name = "[#" + Team.get(data.id).color.toString() + "]" + data.id;
        }
        discussion = true;
        configDoors(false);
    }

    public void configDoors(boolean config) {
        for (Door door : doors) {
            door.tile.build.configure(config);
        }
    }

    public void genTiles() {
        for (Tile tile : Vars.world.tiles) {
            if (tile.build != null && tile.isCenter()) {

                if (tile.build.block == Blocks.doorLarge) {
                    doors.add(new Door(tile));
                    tile.build.configure(true);
                } else if (tile.build.block == Blocks.shockMine) {
                    tile.build.kill();
                    traps.add(new Trapdoor(tile));
                    tile.setFloorNet(Blocks.darkPanel1);
                }
            }
        }
        Vars.world.tile(0, 0).setFloorNet(Blocks.stone);
        Vars.world.tile(1, 0).setFloorNet(Blocks.stone);
        Vars.world.tile(0, 1).setFloorNet(Blocks.stone);
        Vars.world.tile(1, 1).setFloorNet(Blocks.stone);
    }

    public void endDiscussion() {
        if (PlayerData.ALL.size > 0) {
            PlayerData most = PlayerData.ALL.get(0);
            for (PlayerData data : PlayerData.ALL) {
                if (data.votes > most.votes) {
                    most = data;
                }
            }
            if (most.votes > skip) {
                most.clear();
            } else {
                skip();
            }
            for (PlayerData data : PlayerData.ALL) {
                data.player.name = "[black][]";
                data.votes = 0;
                data.hasVoted = false;
            }
        }
        Call.hideHudText();
        time = DISCUSSION_TIME;
        discussion = false;
        configDoors(true);

        voted = 0;
        skip = 0;
    }

    public void skip() {
        Call.sendMessage("[white]Skipped", "Amogus", Nulls.player);
    }

    public void endGame(boolean imposter) {
        ended = true;
        if (imposter) {
            String t = "[white] |";
            for (Imposter imposter1 : Imposter.ALL) {
                t += " " + MainX.NAMES.get(imposter1.player.id);
            }
            Call.infoMessage("[red]Imposters" + t + "[gold] win!");
        } else {
            String t = "[white] |";
            for (Crewmate crewmate : Crewmate.ALL) {
                t += " " + MainX.NAMES.get(crewmate.player.id);
            }
            Call.infoMessage("[accent]Crewmates" + t + "[gold] win!");
        }

        Timer.schedule(() -> {
            Game.ME.lobby.go();
        }, 3);
    }
    
    void connect(int x1, int y1, int x2, int y2) {
        traps.find(t -> (t.tile.x == x1 && t.tile.y == y1)).connect(traps.find(t -> (t.tile.x == x2 && t.tile.y == y2)));
    }
}
