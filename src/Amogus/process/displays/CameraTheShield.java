package Amogus.process.displays;

import Amogus.process.Display;
import Amogus.utils.PlayerData;
import arc.graphics.Color;
import arc.util.Interval;
import mindustry.content.Fx;
import mindustry.gen.Call;

public class CameraTheShield extends Display{
    Interval interval;

    public CameraTheShield(int x1, int y1, int x2, int y2) {
        super(x1, y1, x2, y2);
        interval = new Interval(1);
    }

    @Override
    public void update(PlayerData data) {
        for (PlayerData pd : PlayerData.ALL) {
            if (interval.get(0, 30f)) {
                Call.effect(Fx.bubble, pd.player.x, pd.player.y, 0, Color.black);
            }
        }
    }
}
