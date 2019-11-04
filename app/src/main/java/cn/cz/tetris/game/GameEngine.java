package cn.cz.tetris.game;

import android.content.Context;
import android.os.Bundle;
import android.util.Pair;
import android.util.SparseIntArray;

import java.util.ArrayList;
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
    private boolean mUseFirst = false;
    private int[][] mMovingCoordinates;
    private int[] mTempColors;
    private SparseIntArray mTempBorders;
    private int[] mTempRotateArea;
    private int[] mIndexArray;
    private int[][] mTranslateBorders;
    private ArrayList<int[]> mArrayList;

    private boolean mStarted = false;
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
        mTempRotateArea = new int[4];
        mIndexArray = new int[GameConstants.PIECE_SIZE];
        mTranslateBorders = new int[GameConstants.PIECE_SIZE][2];
        mArrayList = new ArrayList<>();
        for (int i = 0; i != GameConstants.PORT_SIZE; i++) {
            mArrayList.add(new int[GameConstants.LAND_SIZE]);
        }

        mPiecePair = new Pair<>(new Piece(), new Piece());
        mPiecePair.first.refresh(mLevel);
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

    public int getLevel() {
        return mLevel;
    }

    public boolean isStarted() {
        return mStarted;
    }

    public Piece getNextPiece() {
        return mUseFirst ? mPiecePair.second : mPiecePair.first;
    }

    public void onSave(Bundle outState) {
        int[] blocks = new int[GameConstants.LAND_SIZE * (GameConstants.PORT_SIZE + GameConstants.PIECE_SIZE)];
        for (int i = 0; i != GameConstants.PORT_SIZE + GameConstants.PIECE_SIZE; i++) {
            for (int j = 0; j != GameConstants.LAND_SIZE; j++) {
                blocks[i * GameConstants.LAND_SIZE + j] = mBlocks[i][j];
            }
        }
        outState.putIntArray("blocks", blocks);
        outState.putIntArray("renderer_data", mRendererData);
        outState.putParcelable("piece_first", mPiecePair.first);
        outState.putParcelable("piece_second", mPiecePair.second);
        outState.putBoolean("use_first", mUseFirst);
        int[] movingCoordinates = new int[GameConstants.PIECE_SIZE * GameConstants.PIECE_SIZE * 2];
        for (int i = 0; i != GameConstants.PIECE_SIZE * GameConstants.PIECE_SIZE; i++) {
            for (int j = 0; j != 2; j++) {
                movingCoordinates[i * 2 + j] = mMovingCoordinates[i][j];
            }
        }
        outState.putIntArray("moving_coordinates", movingCoordinates);
        outState.putIntArray("temp_colors", mTempColors);
        int size = mTempBorders.size();
        if (size != 0) {
            int[] keys = new int[size];
            int[] values = new int[size];
            for (int i = 0; i != size; i++) {
                keys[i] = mTempBorders.keyAt(i);
                values[i] = mTempBorders.get(keys[i]);
            }
            outState.putIntArray("temp_borders_keys", keys);
            outState.putIntArray("temp_borders_values", values);
        }
        outState.putIntArray("temp_rotate_area", mTempRotateArea);
        outState.putIntArray("index_array", mIndexArray);
        int[] translateBorders = new int[GameConstants.PIECE_SIZE * 2];
        for (int i = 0; i != GameConstants.PIECE_SIZE; i++) {
            for (int j = 0; j != 2; j++) {
                translateBorders[i * 2 + j] = mTranslateBorders[i][j];
            }
        }
        outState.putIntArray("translate_borders", translateBorders);
        int[] arrayList = new int[GameConstants.LAND_SIZE * GameConstants.PORT_SIZE];
        for (int i = 0; i != GameConstants.PORT_SIZE; i++) {
            for (int j = 0; j != GameConstants.LAND_SIZE; j++) {
                arrayList[i * GameConstants.LAND_SIZE + j] = mArrayList.get(i)[j];
            }
        }
        outState.putIntArray("array_list", arrayList);
        outState.putBoolean("started", mStarted);
        outState.putBoolean("is_fast_mode", mIsFastMode);
        outState.putBoolean("game_over", mGameOver);
        outState.putBoolean("is_pause", mIsPause);
        outState.putInt("score", mScore);
    }

    public void onLoad(Bundle savedInstanceState) {
        int[] blocks = savedInstanceState.getIntArray("blocks");
        if (blocks != null) {
            for (int i = 0; i != GameConstants.PORT_SIZE + GameConstants.PIECE_SIZE; i++) {
                for (int j = 0; j != GameConstants.LAND_SIZE; j++) {
                    mBlocks[i][j] = blocks[i * GameConstants.LAND_SIZE + j];
                }
            }
        }
        mRendererData = savedInstanceState.getIntArray("renderer_data");
        mPiecePair = new Pair<>((Piece)savedInstanceState.getParcelable("piece_first"), (Piece)savedInstanceState.getParcelable("piece_second"));
        mUseFirst = savedInstanceState.getBoolean("use_first");
        int[] movingCoordinates = savedInstanceState.getIntArray("moving_coordinates");
        if (movingCoordinates != null) {
            for (int i = 0; i != GameConstants.PIECE_SIZE * GameConstants.PIECE_SIZE; i++) {
                for (int j = 0; j != 2; j++) {
                    mMovingCoordinates[i][j] = movingCoordinates[i * 2 + j];
                }
            }
        }
        mTempColors = savedInstanceState.getIntArray("temp_colors");
        int[] keys = savedInstanceState.getIntArray("temp_borders_keys");
        int[] values = savedInstanceState.getIntArray("temp_borders_values");
        if (keys != null && values != null) {
            for (int i = 0; i != keys.length; i++) {
                mTempBorders.put(keys[i], values[i]);
            }
        }
        mTempRotateArea = savedInstanceState.getIntArray("temp_rotate_area");
        mIndexArray = savedInstanceState.getIntArray("index_array");
        int[] translateBorders = savedInstanceState.getIntArray("translate_borders");
        if (translateBorders != null) {
            for (int i = 0; i != GameConstants.PIECE_SIZE; i++) {
                for (int j = 0; j != 2; j++) {
                    mTranslateBorders[i][j] = translateBorders[i * 2 + j];
                }
            }
        }
        int[] arrayList = savedInstanceState.getIntArray("array_list");
        if (arrayList != null) {
            for (int i = 0; i != GameConstants.PORT_SIZE; i++) {
                for (int j = 0; j != GameConstants.LAND_SIZE; j++) {
                    mArrayList.get(i)[j] = arrayList[i * GameConstants.LAND_SIZE + j];
                }
            }
        }
        mStarted = savedInstanceState.getBoolean("started");
        mIsFastMode = savedInstanceState.getBoolean("is_fast_mode");
        mGameOver = savedInstanceState.getBoolean("game_over");
        mIsPause = savedInstanceState.getBoolean("is_pause");
        mScore = savedInstanceState.getInt("score");
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
                    iGameInterface.onScoreAdded(mScore, mIndexArray);
                }
            }
        } else {
            move(MOVE_TYPE.DOWN);
        }
    }

    public void startGame() {
        mStarted = true;
        addNewPiece();
    }

    public void reset() {
        mStarted = false;
        mIsFastMode = false;
        mGameOver = false;
        mIsPause = false;
        mScore = 0;
        mPiecePair.first.reset();
        mPiecePair.second.reset();
        mUseFirst = false;
        resetMovingCoordinates();
        for (int[] ints : mBlocks) {
            Arrays.fill(ints, 0);
        }
        mPiecePair.first.refresh(mLevel);
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
        if (mIsFastMode) {
            return;
        }

        for (int[] ints : mTranslateBorders) {
            ints[0] = ints[1] = GameConstants.INVALID_VALUE;
        }

        if (isLeft) {
            int left = GameConstants.LAND_SIZE;
            for (int[] ints : mMovingCoordinates) {
                if (ints[1] != GameConstants.INVALID_VALUE && ints[1] < left) {
                    left = ints[1];
                }

                for (int i = 0; i != GameConstants.PIECE_SIZE; i++) {
                    if (mTranslateBorders[i][0] == GameConstants.INVALID_VALUE) {
                        mTranslateBorders[i][0] = ints[0];
                        mTranslateBorders[i][1] = ints[1];
                        break;
                    } else if (mTranslateBorders[i][0] == ints[0]) {
                        if (ints[1] < mTranslateBorders[i][1]) {
                            mTranslateBorders[i][1] = ints[1];
                        }
                        break;
                    }
                }
            }

            if (left != 0) {
                boolean b = true;
                for (int[] ints : mTranslateBorders) {
                    if (ints[0] != GameConstants.INVALID_VALUE && mBlocks[ints[0]][ints[1] - 1] != 0) {
                        b = false;
                        break;
                    }
                }

                if (b) {
                    move(MOVE_TYPE.LEFT);
                }
            }
        } else {
            int right = -1;
            for (int[] ints : mMovingCoordinates) {
                if (ints[1] != GameConstants.INVALID_VALUE && ints[1] > right) {
                    right = ints[1];
                }

                for (int i = 0; i != GameConstants.PIECE_SIZE; i++) {
                    if (mTranslateBorders[i][0] == GameConstants.INVALID_VALUE) {
                        mTranslateBorders[i][0] = ints[0];
                        mTranslateBorders[i][1] = ints[1];
                        break;
                    } else if (mTranslateBorders[i][0] == ints[0]) {
                        if (ints[1] > mTranslateBorders[i][1]) {
                            mTranslateBorders[i][1] = ints[1];
                        }
                        break;
                    }
                }
            }

            if (right != GameConstants.LAND_SIZE - 1) {
                boolean b = true;
                for (int[] ints : mTranslateBorders) {
                    if (ints[0] != GameConstants.INVALID_VALUE && mBlocks[ints[0]][ints[1] + 1] != 0) {
                        b = false;
                        break;
                    }
                }

                if (b) {
                    move(MOVE_TYPE.RIGHT);
                }
            }
        }
    }

    public void rotate() {
        if (mIsPause) {
            return;
        }

        Piece piece = mUseFirst ? mPiecePair.first : mPiecePair.second;
        int[] rotateArea = canRotate(piece);
        if (rotateArea != null) {
            for (int i = 0; i != mTempColors.length; i++) {
                if (mMovingCoordinates[i][0] != GameConstants.INVALID_VALUE) {
                    mTempColors[i] = mBlocks[mMovingCoordinates[i][0]][mMovingCoordinates[i][1]];
                    mBlocks[mMovingCoordinates[i][0]][mMovingCoordinates[i][1]] = 0;
                }
            }

            int maxSize = piece.rotateType == Piece.ROTATE_4 ? 4 : 3;
            for (int i = 0; i != mTempColors.length; i++) {
                if (mMovingCoordinates[i][0] != GameConstants.INVALID_VALUE) {
                    int offset = mMovingCoordinates[i][0] - rotateArea[0];
                    mMovingCoordinates[i][0] = rotateArea[0] + mMovingCoordinates[i][1] - rotateArea[1];
                    mMovingCoordinates[i][1] = rotateArea[1] + (maxSize - 1 - offset);
                    mBlocks[mMovingCoordinates[i][0]][mMovingCoordinates[i][1]] = mTempColors[i];
                }
            }
        }
    }

    public void clearLines() {
        for (int i = 0; i != GameConstants.PORT_SIZE; i++) {
            for (int j = 0; j != GameConstants.LAND_SIZE; j++) {
                mArrayList.get(i)[j] = mBlocks[GameConstants.PORT_SIZE + GameConstants.PIECE_SIZE - i - 1][j];
            }
        }

        int offset = 0;
        for (int i = mIndexArray.length - 1; i >= 0; i--) {
            if (mIndexArray[i] != GameConstants.INVALID_VALUE) {
                int[] array = mArrayList.remove(GameConstants.PORT_SIZE - mIndexArray[i] - offset - 1);
                offset++;
                Arrays.fill(array, 0);
                mArrayList.add(array);
            }
        }

        for (int i = 0; i != GameConstants.PORT_SIZE; i++) {
            for (int j = 0; j != GameConstants.LAND_SIZE; j++) {
                mBlocks[GameConstants.PORT_SIZE + GameConstants.PIECE_SIZE - i - 1][j] = mArrayList.get(i)[j];
            }
        }
    }

    private void move(MOVE_TYPE type) {
        if (mIsPause) {
            return;
        }

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

    private int[] canRotate(Piece piece) {
        if (piece.rotateType == Piece.ROTATE_NONE) {
            return null;
        }

        mTempRotateArea[0] = mTempRotateArea[1] = Integer.MAX_VALUE;
        mTempRotateArea[2] = mTempRotateArea[3] = Integer.MIN_VALUE;
        int maxSize = piece.rotateType == Piece.ROTATE_4 ? 4 : 3;
        for (int[] ints : mMovingCoordinates) {
            if (ints[0] != GameConstants.INVALID_VALUE) {
                if (ints[0] < mTempRotateArea[0]) {
                    mTempRotateArea[0] = ints[0];
                }

                if (ints[1] < mTempRotateArea[1]) {
                    mTempRotateArea[1] = ints[1];
                }

                if (ints[0] > mTempRotateArea[2]) {
                    mTempRotateArea[2] = ints[0];
                }

                if (ints[1] > mTempRotateArea[3]) {
                    mTempRotateArea[3] = ints[1];
                }
            }
        }

        if (mTempRotateArea[2] - mTempRotateArea[0] + 1 < maxSize) {
            int size = maxSize - (mTempRotateArea[2] - mTempRotateArea[0] + 1);
            for (int i = 0; i != size; i++) {
                if ((i & 1) == 0) {
                    if (mTempRotateArea[2] == GameConstants.PORT_SIZE + GameConstants.PIECE_SIZE - 1) {
                        mTempRotateArea[0]--;
                    } else {
                        mTempRotateArea[2]++;
                    }
                } else {
                    mTempRotateArea[0]--;
                }
            }
        } else if (mTempRotateArea[3] - mTempRotateArea[1] + 1 < maxSize) {
            int size = maxSize - (mTempRotateArea[3] - mTempRotateArea[1] + 1);
            for (int i = 0; i != size; i++) {
                if ((i & 1) == 0) {
                    if (mTempRotateArea[1] == 0) {
                        mTempRotateArea[3]++;
                    } else {
                        mTempRotateArea[1]--;
                    }
                } else {
                    if (mTempRotateArea[3] == GameConstants.LAND_SIZE - 1) {
                        mTempRotateArea[1]--;
                    } else {
                        mTempRotateArea[3]++;
                    }
                }
            }
        }

        for (int i = mTempRotateArea[0]; i <= mTempRotateArea[2]; i++) {
            for (int j = mTempRotateArea[1]; j <= mTempRotateArea[3]; j++) {
                if (mBlocks[i][j] != 0) {
                    boolean res = false;
                    for (int[] ints : mMovingCoordinates) {
                        if (i == ints[0] && j == ints[1]) {
                            res = true;
                            break;
                        }
                    }

                    if (!res) {
                        return null;
                    }
                }
            }
        }

        return mTempRotateArea;
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
        Arrays.fill(mIndexArray, GameConstants.INVALID_VALUE);
        int n = 0;
        for (int i = 0; i != GameConstants.PORT_SIZE; i++) {
            n++;
            boolean res = true;
            for (int color : mBlocks[i + GameConstants.PIECE_SIZE]) {
                if (color == 0) {
                    n--;
                    res = false;
                    break;
                }
            }

            if (res) {
                mIndexArray[n - 1] = i;
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
        Piece currentPiece, nextPiece;
        mUseFirst = !mUseFirst;
        if (mUseFirst) {
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
