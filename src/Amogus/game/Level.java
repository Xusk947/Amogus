package Amogus.game;

import Amogus.Game;
import Amogus.MainX;
import Amogus.process.Door;
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

public class Level implements StateX {

    public final int DISCUSSION_TIME = 30;
    public int TEAM_ID = 65;

    public String name;

    public Speed speed = Speed.NORMAL;

    public boolean gameStarted = false;
    public boolean discussion = false;

    public int time = DISCUSSION_TIME;

    public int needImposters = 1;
    public int imposters = 0;

    public Seq<PlayerData> datas;
    public Seq<Door> doors;

    public int skip = 0;

    Interval interval;

    public Level() {
        interval = new Interval(2);
    }

    @Override
    public void go() {
        DeadBody.ALL = new Seq<>();
        Game.ME.state = Game.State.Game;
        Seq<Player> players = new Seq<>();
        datas = new Seq<>();
        doors = new Seq<>();
        discussion = false;
        gameStarted = false;
        time = DISCUSSION_TIME;
        Groups.player.copy(players);
        imposters = 0;
        players.shuffle();

        Vars.logic.reset();

        Call.worldDataBegin();
        Vars.world.loadMap(Vars.maps.byName(name));
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
        if (gameStarted) {
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
                        }
                    }
                }
            }
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
                data.id = data.player.team().id;
            }
        }

        for (Tile tile : Vars.world.tiles) {
            if (tile.build != null && tile.isCenter()) {

                if (tile.build.block == Blocks.doorLarge) {
                    doors.add(new Door(tile));
                    tile.build.configure(true);
                } else if (tile.build.block == Blocks.shockMine) {
                    tile.build.kill();
                    tile.setFloorNet(Blocks.darkPanel1);
                }
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

    public void endDiscussion() {
        if (PlayerData.ALL.size > 0) {
            PlayerData most = PlayerData.ALL.get(0);
            for (PlayerData data : PlayerData.ALL) {
                if (data.votes > most.votes) {
                    most = data;
                }
                Log.info(data.votes + " : " + most.votes);
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
        skip = 0;
    }

    public void skip() {
        Call.sendMessage("[white]Skipped", "Amogus", Nulls.player);
    }
}
