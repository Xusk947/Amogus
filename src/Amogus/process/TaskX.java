package Amogus.process;

import Amogus.utils.Crewmate;
import mindustry.world.Tile;

public abstract class TaskX {
    
    public Tile tile;
    
    public TaskX(Tile tile) {
        this.tile = tile;
    }
    
    public abstract void update(Crewmate data);
    
    public abstract void onStart(Crewmate data);

    public abstract TaskX start();
}
