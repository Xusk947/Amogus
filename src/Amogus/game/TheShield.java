package Amogus.game;

import Amogus.Game;
import Amogus.process.Trapdoor;
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
        
        // Reactor | Upper engine | Lower Engine
        connect(39, 29, 25, 52);
        connect(22, 60, 39, 78);
        // Electric | Med Bay | Security
        connect(45, 53, 53, 47);
        connect(45, 53, 54, 57);
        connect(54, 57, 53, 47);
        // Navigation | Shields | Weapons
        connect(102, 33, 116, 46);
        connect(116, 53, 101, 71);
        // Admin | Cafeteria | Bruh
        connect(102, 43, 82, 42);
//        connect(82, 42, time, time);
    }
}
