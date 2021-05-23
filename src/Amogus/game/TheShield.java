package Amogus.game;

import Amogus.Game;
import Amogus.process.tasks.WirelessTask;
import Amogus.utils.PlayerData;
import mindustry.Vars;

public class TheShield extends Level {

    public TheShield() {
        super();
        name = "the_shield";
    }

    @Override
    public void update() {
        super.update();
        if (datas.size <= 0) {
            Game.ME.lobby.go();
        }
    }

    @Override
    public void cons(PlayerData data) {
        super.cons(data);
    }

    @Override
    public void genTiles() {
        super.genTiles();
        tasks.add(new WirelessTask(Vars.world.tile(53, 49)));
        tasks.add(new WirelessTask(Vars.world.tile(78, 47)));
    }
}
