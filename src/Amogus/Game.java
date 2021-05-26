package Amogus;

import Amogus.game.Level;
import Amogus.game.TheShield;
import Amogus.lobby.Lobby;

public class Game {

    public static Game ME;

    public enum State {
        Lobby, Game
    }

    public Lobby lobby;
    public Level level;
    public State state;

    public Game() {
        ME = this;
        lobby = new Lobby();
        switch (Config.map) {
            case THE_SHIELD:
                level = new TheShield();
                break;
        }
    }

    public void update() {
        switch (state) {
            case Lobby:
                lobby.update();
                break;
            case Game:
                level.update();
                break;
        }
    }
}
