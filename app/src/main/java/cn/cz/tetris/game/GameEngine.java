package cn.cz.tetris.game;

import android.content.Context;

import java.util.Arrays;
import java.util.Timer;
import java.util.TimerTask;

import cn.cz.tetris.utils.SPUtils;

public class GameEngine {
    private static final String TAG = "GameEngine";

    private Context mContext;
    private int[][] mBlocks;
    private Timer mTimer;
    private int mScore = 0;
    private int mMaxScore = 0;

    // 设置信息
    private int mLevel;
    private long mSpeed;

    public GameEngine(Context context) {
        mContext = context;
        mMaxScore = SPUtils.getMaxScore(mContext);
        readSetting();

        mBlocks = new int[GameConstants.PORT_SIZE][GameConstants.LAND_SIZE];
        for (int[] ints : mBlocks) {
            Arrays.fill(ints, 0);
        }
    }

    public int[][] getBlocks() {
        return mBlocks;
    }

    public void startGame() {
        if (mTimer == null) {
            mTimer = new Timer();
            mTimer.scheduleAtFixedRate(new TimerTask() {
                @Override
                public void run() {

                }
            }, GameConstants.TIME_PERIOD, GameConstants.TIME_PERIOD);
        }
    }

    public void stopGame() {
        if (mTimer != null) {
            mTimer.cancel();
            mTimer.purge();
            mTimer = null;
        }
    }

    public void readSetting() {
        mLevel = SPUtils.getLevel(mContext);
        mSpeed = SPUtils.getSpeed(mContext);
    }
}
