package cn.cz.tetris.game;

import android.content.Context;
import android.util.Pair;
import android.util.SparseIntArray;

import java.util.Arrays;

import cn.cz.tetris.utils.DebugLog;
import cn.cz.tetris.utils.SPUtils;

public class GameEngine {
    private static final String TAG = "GameEngine";

    private enum MOVE_TYPE {
        DOWN,
        LEFT,
        RIGHT
    }

    private Context mContext;
    private IGameInterface iGameInterface;

    private int[][] mBlocks;
    private int[] mRendererData;
    private Pair<Piece, Piece> mPiecePair;
    private boolean mUserFirst = true;
    private int[][] mMovingCoordinates;
    private int[] mTempColors;
    private SparseIntArray mTempBorders;

    private boolean mIsFastMode = false;
    private boolean mGameOver = false;
    private volatile boolean mIsPause = false;
    private int mScore = 0;

    // 设置信息
    private int mLevel;
    private int mSpeed;

    public GameEngine(Context context, IGameInterface gameInterface) {
        mContext = context;
        iGameInterface = gameInterface;
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

    public void setFastMode() {
        mIsFastMode = true;
    }

    public boolean isFastMode() {
        return mIsFastMode;
    }

    public boolean isPaused() {
        return mIsPause;
    }

    public int getScore() {
        return mScore;
    }

    public int getSpeed() {
        return mSpeed;
    }

    public void run() {
        if (mIsPause || mGameOver) {
            return;
        }

        if (needShowNext()) {
            mIsFastMode = false;
            if (isFailed()) {
                mGameOver = true;
                DebugLog.i(TAG, "Game over!");
                if (mScore > SPUtils.getMaxScore(mContext)) {
                    SPUtils.setMaxScore(mContext, mScore);
                }
                iGameInterface.onFailed(mScore);
            } else {
                addNewPiece();
                if (needAddScore()) {
                    iGameInterface.onScoreAdded(mScore);
                }
            }
        } else {
            move(MOVE_TYPE.DOWN);
        }
    }

    public void startGame() {
        addNewPiece();
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

    public void translate(boolean isLeft) {
        if (isLeft) {
            int left = GameConstants.LAND_SIZE;
            for (int[] ints : mMovingCoordinates) {
                if (ints[1] != GameConstants.INVALID_VALUE && ints[1] < left) {
                    left = ints[1];
                }
            }

            if (left != 0) {
                move(MOVE_TYPE.LEFT);
            }
        } else {
            int right = -1;
            for (int[] ints : mMovingCoordinates) {
                if (ints[1] != GameConstants.INVALID_VALUE && ints[1] > right) {
                    right = ints[1];
                }
            }

            if (right != GameConstants.LAND_SIZE - 1) {
                move(MOVE_TYPE.RIGHT);
            }
        }
    }

    public void rotate() {
        Piece piece = mUserFirst ? mPiecePair.first : mPiecePair.second;
        if (canRotate(piece)) {

        }
    }

    private void move(MOVE_TYPE type) {
        for (int i = 0; i != mTempColors.length; i++) {
            if (mMovingCoordinates[i][0] != GameConstants.INVALID_VALUE) {
                mTempColors[i] = mBlocks[mMovingCoordinates[i][0]][mMovingCoordinates[i][1]];
                mBlocks[mMovingCoordinates[i][0]][mMovingCoordinates[i][1]] = 0;
            }
        }

        for (int i = 0; i != mTempColors.length; i++) {
            if (mMovingCoordinates[i][0] != GameConstants.INVALID_VALUE) {
                switch (type) {
                    case DOWN:
                        mBlocks[++mMovingCoordinates[i][0]][mMovingCoordinates[i][1]] = mTempColors[i];
                        break;
                    case LEFT:
                        mBlocks[mMovingCoordinates[i][0]][--mMovingCoordinates[i][1]] = mTempColors[i];
                        break;
                    case RIGHT:
                        mBlocks[mMovingCoordinates[i][0]][++mMovingCoordinates[i][1]] = mTempColors[i];
                        break;
                }
            }
        }
    }

    private boolean canRotate(Piece piece) {
        if (piece.rotateType == Piece.ROTATE_NONE) {
            return false;
        }

        int maxSize = piece.rotateType == Piece.ROTATE_4 ? 4 : 3;
        for (int[] ints : mMovingCoordinates) {
            if (ints[0] != GameConstants.INVALID_VALUE) {

            }
        }

        return false;
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

    private boolean isFailed() {
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
}
