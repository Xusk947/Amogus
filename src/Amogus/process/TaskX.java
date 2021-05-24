package Amogus.process;

import Amogus.utils.Crewmate;
import mindustry.gen.Call;
import mindustry.world.Tile;

public abstract class TaskX {
    
    public Tile tile;
    
    public TaskX(Tile tile) {
        this.tile = tile;
    }
    
    public abstract void update(Crewmate data);
    
    public abstract void onStart(Crewmate data);
    
    public void onFinish(Crewmate data) {
        data.currentTask = null;
        data.finished.add(tile);
        Call.label(data.player.con, "[lime]Finished!", 3, tile.drawx(), tile.drawy());
    };

    public abstract TaskX start();
    
}
