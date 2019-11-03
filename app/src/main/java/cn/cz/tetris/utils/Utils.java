package cn.cz.tetris.utils;

import android.content.Context;

import cn.cz.tetris.R;
import cn.cz.tetris.game.GameConstants;

public class Utils {
    public static int getDisplayWidth(Context context) {
        return context.getResources().getDisplayMetrics().widthPixels;
    }

    public static int getDisplayHeight(Context context) {
        return context.getResources().getDisplayMetrics().heightPixels;
    }

    public static String[] getLevelStrings(Context context) {
        return new String[] {
                context.getString(R.string.level_normal),
                context.getString(R.string.level_hard),
                context.getString(R.string.level_nightmare)
        };
    }

    public static String[] getSpeedStrings(Context context) {
        return new String[] {
                context.getString(R.string.speed_very_slow),
                context.getString(R.string.speed_slow),
                context.getString(R.string.speed_normal),
                context.getString(R.string.speed_fast),
                context.getString(R.string.speed_very_fast)
        };
    }

    public static String[] getMusicStrings(Context context) {
        return new String[] {
                context.getString(R.string.music_random),
                context.getString(R.string.music_origin),
                context.getString(R.string.music_katyusha),
                context.getString(R.string.music_victory_day),
                context.getString(R.string.music_marche_slave),
                context.getString(R.string.music_red_strong)
        };
    }

    public static int[] getSpeeds() {
        return new int[] {
                GameConstants.SPEED_VERY_SLOW,
                GameConstants.SPEED_SLOW,
                GameConstants.SPEED_NORMAL,
                GameConstants.SPEED_FAST,
                GameConstants.SPEED_VERY_FAST
        };
    }

    public static int getSpeedIndex(int speed, int[] speeds) {
        int index = 0;
        for (int i = 0; i != speeds.length; i++) {
            if (speed == speeds[i]) {
                index = i;
                break;
            }
        }
        return index;
    }

    public static int[] getColors(Context context) {
        return new int[] {
                0,
                context.getResources().getColor(R.color.red),
                context.getResources().getColor(R.color.green),
                context.getResources().getColor(R.color.blue)
        };
    }
}
