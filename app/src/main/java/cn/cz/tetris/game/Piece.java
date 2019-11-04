package cn.cz.tetris.game;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Arrays;
import java.util.Random;

public class Piece implements Parcelable {
    public static final int ROTATE_NONE = 0;
    public static final int ROTATE_3 = 1;
    public static final int ROTATE_4 = 2;

    public int color;
    public int rotateType;
    public int[][] blocks;

    private Random mRandom;

    public Piece() {
        color = 0;
        rotateType = ROTATE_NONE;
        blocks = new int[GameConstants.PIECE_SIZE][GameConstants.PIECE_SIZE];
        resetBlocks();

        mRandom = new Random();
    }

    void reset() {
        color = 0;
        rotateType = ROTATE_NONE;
        resetBlocks();
    }

    void refresh(int level) {
        resetBlocks();
        long timestamp = System.currentTimeMillis();
        switch ((int)(timestamp % 6L)) {
            case 0:
                color = GameConstants.COLOR_RED;
                break;
            case 1:
                color = GameConstants.COLOR_GREEN;
                break;
            case 2:
                color = GameConstants.COLOR_BLUE;
                break;
            case 3:
                color = GameConstants.COLOR_YELLOW;
                break;
            case 4:
                color = GameConstants.COLOR_PINK;
                break;
            case 5:
                color = GameConstants.COLOR_WATER;
                break;
        }

        mRandom.setSeed(timestamp);
        int pieceType = 0;
        switch (level) {
            case GameConstants.LEVEL_NORMAL:
                pieceType = mRandom.nextInt(7);
                break;
            case GameConstants.LEVEL_HARD:
                pieceType = mRandom.nextInt(12);
                break;
            case GameConstants.LEVEL_NIGHTMARE:
                pieceType = mRandom.nextInt(17);
                break;
        }

        switch (pieceType) {
            // 普通难度
            case 0:
                // O
                blocks[2][1] = blocks[2][2] = blocks[3][1] = blocks[3][2] = color;
                rotateType = ROTATE_NONE;
                break;
            case 1:
                // T
                blocks[2][1] = blocks[2][2] = blocks[3][1] = blocks[1][1] = color;
                rotateType = ROTATE_3;
                break;
            case 2:
                // I
                blocks[0][1] = blocks[1][1] = blocks[2][1] = blocks[3][1] = color;
                rotateType = ROTATE_4;
                break;
            case 3:
                // Z
                blocks[2][1] = blocks[2][2] = blocks[3][1] = blocks[1][2] = color;
                rotateType = ROTATE_3;
                break;
            case 4:
                // Z
                blocks[2][1] = blocks[2][2] = blocks[3][2] = blocks[1][1] = color;
                rotateType = ROTATE_3;
                break;
            case 5:
                // L
                blocks[3][1] = blocks[3][2] = blocks[2][1] = blocks[1][1] = color;
                rotateType = ROTATE_3;
                break;
            case 6:
                // L
                blocks[3][1] = blocks[3][2] = blocks[2][2] = blocks[1][2] = color;
                rotateType = ROTATE_3;
                break;
            // 困难难度
            case 7:
                blocks[0][1] = blocks[0][2] = blocks[1][1] = blocks[1][2] = blocks[2][1] = blocks[2][2] = blocks[3][1] = blocks[3][2] = color;
                rotateType = ROTATE_4;
                break;
            case 8:
                blocks[1][1] = blocks[1][2] = blocks[2][1] = blocks[3][1] = blocks[3][2] = color;
                rotateType = ROTATE_3;
                break;
            case 9:
                blocks[1][1] = blocks[1][2] = blocks[2][1] = blocks[2][2] = blocks[3][1] = color;
                rotateType = ROTATE_3;
                break;
            case 10:
                blocks[1][1] = blocks[1][2] = blocks[2][1] = blocks[2][2] = blocks[3][2] = color;
                rotateType = ROTATE_3;
                break;
            case 11:
                blocks[0][1] = blocks[1][1] = blocks[1][2] = blocks[2][1] = blocks[2][2] = blocks[3][1] = color;
                rotateType = ROTATE_4;
                break;
            // 噩梦难度
            case 12:
                // Big O
                blocks[0][0] = blocks[0][1] = blocks[0][2] = blocks[0][3] = blocks[1][0] = blocks[1][3] = blocks[2][0] = blocks[2][3] = blocks[3][0] = blocks[3][1] = blocks[3][2] = blocks[3][3] = color;
                rotateType = ROTATE_NONE;
                break;
            case 13:
                // Big I
                blocks[0][0] = blocks[0][1] = blocks[0][2] = blocks[0][3] = blocks[1][1] = blocks[1][2] = blocks[2][1] = blocks[2][2] = blocks[3][0] = blocks[3][1] = blocks[3][2] = blocks[3][3] = color;
                rotateType = ROTATE_4;
                break;
            case 14:
                // Big +
                blocks[0][1] = blocks[0][2] = blocks[1][0] = blocks[1][1] = blocks[1][2] = blocks[1][3] = blocks[2][0] = blocks[2][1] = blocks[2][2] = blocks[2][3] = blocks[3][1] = blocks[3][2] = color;
                rotateType = ROTATE_NONE;
                break;
            case 15:
                // Big Z
                blocks[0][1] = blocks[0][2] = blocks[0][3] = blocks[1][1] = blocks[1][2] = blocks[2][1] = blocks[2][2] = blocks[3][0] = blocks[3][1] = blocks[3][2] = color;
                rotateType = ROTATE_4;
                break;
            case 16:
                // Big Z
                blocks[0][0] = blocks[0][1] = blocks[0][2] = blocks[1][1] = blocks[1][2] = blocks[2][1] = blocks[2][2] = blocks[3][1] = blocks[3][2] = blocks[3][3] = color;
                rotateType = ROTATE_4;
                break;
        }
    }

    private void resetBlocks() {
        for (int[] ints : blocks) {
            Arrays.fill(ints, 0);
        }
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(color);
        dest.writeInt(rotateType);
        int[] ints = new int[GameConstants.PIECE_SIZE * GameConstants.PIECE_SIZE];
        for (int i = 0; i != GameConstants.PIECE_SIZE; i++) {
            for (int j = 0; j != GameConstants.PIECE_SIZE; j++) {
                ints[i * GameConstants.PIECE_SIZE + j] = blocks[i][j];
            }
        }
        dest.writeIntArray(ints);
    }

    private static void loadParcel(Parcel p, Piece piece) {
        piece.color = p.readInt();
        piece.rotateType = p.readInt();
        int[] ints = new int[GameConstants.PIECE_SIZE * GameConstants.PIECE_SIZE];
        p.readIntArray(ints);
        for (int i = 0; i != GameConstants.PIECE_SIZE; i++) {
            for (int j = 0; j != GameConstants.PIECE_SIZE; j++) {
                piece.blocks[i][j] = ints[i * GameConstants.PIECE_SIZE + j];
            }
        }
    }

    public static Creator<Piece> CREATOR = new Creator<Piece>() {
        @Override
        public Piece createFromParcel(Parcel source) {
            Piece piece = new Piece();
            loadParcel(source, piece);
            return piece;
        }

        @Override
        public Piece[] newArray(int size) {
            return new Piece[size];
        }
    };
}
