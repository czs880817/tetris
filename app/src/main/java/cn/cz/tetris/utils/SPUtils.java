package cn.cz.tetris.utils;

import android.content.Context;

import cn.cz.tetris.game.GameConstants;

public class SPUtils {
    private static final String SP_NAME = "game_sp";

    private static final String KEY_MAX_SCORE = "max_score";
    private static final String KEY_LEVEL = "level";
    private static final String KEY_SPEED = "speed";

    public static void setMaxScore(Context context, int maxScore) {
        context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE).edit().putInt(KEY_MAX_SCORE, maxScore).apply();
    }

    public static int getMaxScore(Context context) {
        return context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE).getInt(KEY_MAX_SCORE, 0);
    }

    public static void setLevel(Context context, int level) {
        context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE).edit().putInt(KEY_LEVEL, level).apply();
    }

    public static int getLevel(Context context) {
        return context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE).getInt(KEY_LEVEL, GameConstants.LEVEL_NORMAL);
    }

    public static void setSpeed(Context context, int speed) {
        context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE).edit().putInt(KEY_SPEED, speed).apply();
    }

    public static int getSpeed(Context context) {
        return context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE).getInt(KEY_SPEED, GameConstants.SPEED_1);
    }
}
