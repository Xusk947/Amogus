package Amogus.game;

import Amogus.Game;
import Amogus.utils.PlayerData;

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

}
