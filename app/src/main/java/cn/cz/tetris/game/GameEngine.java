package cn.cz.tetris.game;

import android.content.Context;
import android.util.Pair;
import android.util.SparseIntArray;

import java.util.Arrays;
import java.util.Timer;
import java.util.TimerTask;

import cn.cz.tetris.utils.DebugLog;
import cn.cz.tetris.utils.SPUtils;

public class GameEngine {
    private static final String TAG = "GameEngine";

    private Context mContext;
    private IGameInterface iGameInterface;

    private int[][] mBlocks;
    private int[] mRendererData;
    private Pair<Piece, Piece> mPiecePair;
    private boolean mUserFirst = true;
    private int[][] mMovingCoordinates;
    private int[] mTempColors;
    private SparseIntArray mTempBorders;

    private Timer mTimer;
    private volatile boolean mIsPause = false;
    private int mScore = 0;
    private int mMaxScore;

    // 设置信息
    private int mLevel;
    private long mSpeed;

    public GameEngine(Context context, IGameInterface gameInterface) {
        mContext = context;
        iGameInterface = gameInterface;
        mMaxScore = SPUtils.getMaxScore(mContext);
        readSetting();

        mBlocks = new int[GameConstants.PORT_SIZE + GameConstants.PIECE_SIZE][GameConstants.LAND_SIZE];
        for (int[] ints : mBlocks) {
            Arrays.fill(ints, 0);
        }

        mRendererData = new int[GameConstants.PORT_SIZE * GameConstants.LAND_SIZE];

        mMovingCoordinates = new int[GameConstants.PIECE_SIZE * GameConstants.PIECE_SIZE][2];
        resetMovingCoordinates();
        mTempColors = new int[GameConstants.PIECE_SIZE * GameConstants.PIECE_SIZE];
        Arrays.fill(mTempColors, 0);
        mTempBorders = new SparseIntArray();

        mPiecePair = new Pair<>(new Piece(), new Piece());
        mPiecePair.first.refresh(mLevel);
        mPiecePair.second.refresh(mLevel);
    }

    public int[][] getBlocks() {
        return mBlocks;
    }

    public void startGame() {
        if (mTimer == null) {
            addNewPiece();
            mTimer = new Timer();
            mTimer.scheduleAtFixedRate(mTimerTask, mSpeed, mSpeed);
        }
    }

    public void stopGame() {
        if (mTimer != null) {
            mTimer.cancel();
            mTimer.purge();
            mTimer = null;
        }
    }

    public void resumeGame() {
        mIsPause = false;
    }

    public void pauseGame() {
        mIsPause = true;
    }

    public void readSetting() {
        mLevel = SPUtils.getLevel(mContext);
        mSpeed = SPUtils.getSpeed(mContext);
    }

    public int[] getRendererData() {
        for (int i = 0; i != GameConstants.PORT_SIZE; i++) {
            for (int j = 0; j != GameConstants.LAND_SIZE; j++) {
                mRendererData[i * GameConstants.LAND_SIZE + j] = mBlocks[GameConstants.PORT_SIZE + GameConstants.PIECE_SIZE - i - 1][j];
            }
        }
        return mRendererData;
    }

    public void dropDown(boolean isFast) {
        if (isFast) {

        } else {
            for (int i = 0; i != mTempColors.length; i++) {
                if (mMovingCoordinates[i][0] != GameConstants.INVALID_VALUE) {
                    mTempColors[i] = mBlocks[mMovingCoordinates[i][0]][mMovingCoordinates[i][1]];
                    mBlocks[mMovingCoordinates[i][0]][mMovingCoordinates[i][1]] = 0;
                }
            }

            for (int i = 0; i != mTempColors.length; i++) {
                if (mMovingCoordinates[i][0] != GameConstants.INVALID_VALUE) {
                    mBlocks[++mMovingCoordinates[i][0]][mMovingCoordinates[i][1]] = mTempColors[i];
                }
            }
        }
    }

    public void translate(boolean isLeft) {
        if (isLeft) {
            int left = GameConstants.LAND_SIZE;
            for (int[] ints : mMovingCoordinates) {
                if (ints[1] != GameConstants.INVALID_VALUE && ints[1] < left) {
                    left = ints[1];
                }
            }

            if (left != 0) {

            }
        } else {
            int right = -1;
            for (int[] ints : mMovingCoordinates) {
                if (ints[1] != GameConstants.INVALID_VALUE && ints[1] > right) {
                    right = ints[1];
                }
            }

            if (right != GameConstants.LAND_SIZE - 1) {

            }
        }
    }

    public void rotate() {

    }

    private boolean needShowNext() {
        mTempBorders.clear();
        for (int[] ints : mMovingCoordinates) {
            if (ints[0] >= mTempBorders.get(ints[1])) {
                mTempBorders.put(ints[1], ints[0]);
            }
        }

        for (int i = 0; i != mTempBorders.size(); i++) {
            int key = mTempBorders.keyAt(i);
            int value = mTempBorders.get(key);
            if (value == GameConstants.PORT_SIZE + GameConstants.PIECE_SIZE - 1 || mBlocks[value + 1][key] != 0) {
                return true;
            }
        }

        return false;
    }

    private boolean isFailing() {
        for (int[] ints : mMovingCoordinates) {
            if (ints[0] != GameConstants.INVALID_VALUE && ints[0] < GameConstants.PIECE_SIZE) {
                return true;
            }
        }

        return false;
    }

    private boolean needAddScore() {
        int n = 0;
        for (int i = 0; i != GameConstants.PORT_SIZE; i++) {
            n++;
            for (int color : mBlocks[i + GameConstants.PIECE_SIZE]) {
                if (color == 0) {
                    n--;
                    break;
                }
            }
        }

        switch (n) {
            case 1:
                mScore += 10;
                break;
            case 2:
                mScore += 30;
                break;
            case 3:
                mScore += 60;
                break;
            case 4:
                mScore += 100;
                break;
        }

        return n != 0;
    }

    private void addNewPiece() {
        mUserFirst = !mUserFirst;
        Piece currentPiece, nextPiece;
        if (mUserFirst) {
            currentPiece = mPiecePair.first;
            nextPiece = mPiecePair.second;
        } else {
            currentPiece = mPiecePair.second;
            nextPiece = mPiecePair.first;
        }

        nextPiece.refresh(mLevel);
        iGameInterface.onPieceChanged(nextPiece);

        resetMovingCoordinates();
        int coordinateIndex = 0;
        for (int i = 0; i != GameConstants.PIECE_SIZE; i++) {
            for (int j = 0; j != GameConstants.PIECE_SIZE; j++) {
                int index = j + (GameConstants.LAND_SIZE - GameConstants.PIECE_SIZE) / 2;
                if (currentPiece.blocks[i][j] != 0) {
                    mBlocks[i][index] = currentPiece.blocks[i][j];
                    mMovingCoordinates[coordinateIndex][0] = i;
                    mMovingCoordinates[coordinateIndex][1] = index;
                    coordinateIndex++;
                }
            }
        }
    }

    private void resetMovingCoordinates() {
        for (int[] ints : mMovingCoordinates) {
            Arrays.fill(ints, GameConstants.INVALID_VALUE);
        }
    }

    private TimerTask mTimerTask = new TimerTask() {
        @Override
        public void run() {
            if (mIsPause) {
                return;
            }

            if (needShowNext()) {
                if (isFailing()) {
                    if (mScore > mMaxScore) {
                        SPUtils.setMaxScore(mContext, mScore);
                    }
                    iGameInterface.onFailed(mScore);
                    stopGame();
                } else {
                    addNewPiece();
                    if (needAddScore()) {
                        iGameInterface.onScoreAdded(mScore);
                    }
                }
            } else {
                dropDown(false);
            }
        }
    };
}
