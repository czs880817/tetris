package cn.cz.tetris.game;

import java.util.Arrays;
import java.util.Timer;
import java.util.TimerTask;

public class GameEngine {
    private static final String TAG = "GameEngine";

    private int[][] mBlocks;
    private Timer mTimer;
    private int mScore = 0;

    public GameEngine() {
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
}
