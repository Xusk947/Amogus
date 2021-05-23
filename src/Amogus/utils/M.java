package Amogus.utils;

import arc.func.Intc2;
import mindustry.Vars;

public class M {

    public static boolean ray(float x0, float y0, float x1, float y1) {
        return ray((int) (x0), (int) (y0), (int) (x1), (int) (y1));
    }

    public static boolean ray(int x0, int y0, int x1, int y1) {
        boolean swapXY = fastAbs(y1 - y0) > fastAbs(x1 - x0);
        int tmp;
        if (swapXY) {
            tmp = x0;
            x0 = y0;
            y0 = tmp; // swap x0 and y0
            tmp = x1;
            x1 = y1;
            y1 = tmp; // swap x1 and y1
        }

        if (x0 > x1) {
            // make sure x0 < x1
            tmp = x0;
            x0 = x1;
            x1 = tmp; // swap x0 and x1
            tmp = y0;
            y0 = y1;
            y1 = tmp; // swap y0 and y1
        }

        int deltax = x1 - x0;
        int deltay = fastAbs(y1 - y0);
        int error = deltax / 2;
        int y = y0;
        int ystep = (y0 < y1 ? 1 : -1);
        if (swapXY) {
            for (int x = x0; x < x1 + 1; x++) {
                if (Vars.world.tile(y, x) != null && Vars.world.tile(y, x).solid()) {
                    return true;
                }
                error -= deltay;
                if (error < 0) {
                    y = y + ystep;
                    error = error + deltax;
                }
            }
        } else {
            for (int x = x0; x < x1 + 1; x++) {
                if (Vars.world.tile(x, y) != null && Vars.world.tile(x, y).solid()) {
                    return true;
                }
                error -= deltay;
                if (error < 0) {
                    y = y + ystep;
                    error = error + deltax;
                }
            }
        }
        return false;
    }

    public static void line(float x0, float y0, float x1, float y1, Intc2 cons) {
        line((int) (x0), (int) (y0), (int) (x1), (int) (y1), (cons));
    }

    public static void line(int x0, int y0, int x1, int y1, Intc2 cons) {
        boolean swapXY = fastAbs(y1 - y0) > fastAbs(x1 - x0);
        int tmp;
        if (swapXY) {
            tmp = x0;
            x0 = y0;
            y0 = tmp; // swap x0 and y0
            tmp = x1;
            x1 = y1;
            y1 = tmp; // swap x1 and y1
        }

        if (x0 > x1) {
            // make sure x0 < x1
            tmp = x0;
            x0 = x1;
            x1 = tmp; // swap x0 and x1
            tmp = y0;
            y0 = y1;
            y1 = tmp; // swap y0 and y1
        }

        int deltax = x1 - x0;
        int deltay = fastAbs(y1 - y0);
        int error = deltax / 2;
        int y = y0;
        int ystep = (y0 < y1 ? 1 : -1);
        if (swapXY) {
            for (int x = x0; x < x1 + 1; x++) {
                cons.get(y, x);
                error -= deltay;
                if (error < 0) {
                    y = y + ystep;
                    error = error + deltax;
                }
            }
        } else {
            for (int x = x0; x < x1 + 1; x++) {
                cons.get(x, y);
                error -= deltay;
                if (error < 0) {
                    y = y + ystep;
                    error = error + deltax;
                }
            }
        }
    }

    public static int fastAbs(int v) {
        return (v ^ (v >> 31)) - (v >> 31);
    }
}
