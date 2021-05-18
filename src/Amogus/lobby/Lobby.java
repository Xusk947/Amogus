package Amogus.lobby;

import Amogus.Game;
import Amogus.MainX;
import Amogus.utils.StateX;
import arc.math.Mathf;
import arc.struct.Seq;
import arc.util.Interval;
import arc.util.Time;
import mindustry.Vars;
import mindustry.game.Team;
import mindustry.gen.Call;
import mindustry.gen.Groups;
import mindustry.gen.Player;

public class Lobby implements StateX {

    public final float SEC = 60f;
    public final float TIME = 60f * 5f;
    public final int MIN_PLAYER = 1;

    public float time = 60 * 30f;
    public Interval interval;

    public Lobby() {
        interval = new Interval(2);
    }

    @Override
    public void update() {
        if (Groups.player.size() >= MIN_PLAYER) {
            if (Team.sharded.core() != null) {
                if (interval.get(0, SEC)) {
                    time = time - Time.delta * 60;
                    Call.label("[accent]Start in: " + Mathf.floor(time / 60), Time.delta, Team.sharded.core().x, Team.sharded.core().y);
                    if (time < 1) {
                        Game.ME.level.go();
                    }
                }
            }
        }
    }

    @Override
    public void go() {
        Game.ME.state = Game.State.Lobby;
        Seq<Player> players = new Seq<>();
        Groups.player.copy(players);

        time = TIME;
        
        Vars.logic.reset();

        Vars.world.loadMap(Vars.maps.byName("ship"));
        Vars.state.rules = MainX.lobby.copy();

        Vars.logic.play();

        for (Player player : players) {
            if (MainX.NAMES.containsKey(player.id)) {
                player.name = MainX.NAMES.get(player.id);
            }
            player.team(Team.sharded);
            Vars.netServer.sendWorldData(player);
        }
    }
}
