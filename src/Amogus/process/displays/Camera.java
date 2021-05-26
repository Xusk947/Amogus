package Amogus.process.displays;

import Amogus.process.Display;
import Amogus.utils.PlayerData;
import Amogus.utils.Rect;
import arc.graphics.Color;
import arc.struct.Seq;
import arc.util.Interval;
import mindustry.content.Fx;
import mindustry.gen.Call;

public class Camera extends Display {

    Seq<Rect> rects;
    Interval interval;

    public Camera(int x1, int y1, int x2, int y2) {
        super(x1, y1, x2, y2);
        rects = new Seq<>();
        interval = new Interval(1);
    }

    @Override
    public void update(PlayerData data) {
        for (PlayerData pd : PlayerData.ALL) {
            for (Rect rect : rects) {
                if (rect.in(pd.player.tileX(), pd.player.tileY())) {
                    Call.effect(Fx.pointHit, pd.player.x, pd.player.y, 0, pd.player.team().color);
                }
            }
        }
    }
}
