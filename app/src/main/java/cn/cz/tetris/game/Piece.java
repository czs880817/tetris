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

    void refresh(int level) {
        resetBlocks();
        mRandom.setSeed(System.currentTimeMillis());
        switch (mRandom.nextInt(3)) {
            case 0:
                color = GameConstants.COLOR_RED;
                break;
            case 1:
                color = GameConstants.COLOR_GREEN;
                break;
            case 2:
                color = GameConstants.COLOR_BLUE;
                break;
        }

        int pieceType = 0;
        switch (level) {
            case GameConstants.LEVEL_NORMAL:
                pieceType = mRandom.nextInt(8);
                break;
            case GameConstants.LEVEL_HARD:
                pieceType = mRandom.nextInt(9);
                break;
            case GameConstants.LEVEL_NIGHTMARE:
                pieceType = mRandom.nextInt(10);
                break;
        }

        switch (pieceType) {
            // 普通难度
            case 0:
                blocks[2][1] = blocks[2][2] = blocks[3][1] = blocks[3][2] = color;
                break;
            case 1:
                blocks[2][1] = blocks[2][2] = blocks[3][1] = blocks[1][1] = color;
                rotateType = ROTATE_3;
                break;
            case 2:
                blocks[2][1] = blocks[2][2] = blocks[3][2] = blocks[1][2] = color;
                rotateType = ROTATE_3;
                break;
            case 3:
                blocks[0][1] = blocks[1][1] = blocks[2][1] = blocks[3][1] = color;
                rotateType = ROTATE_4;
                break;
            case 4:
                blocks[2][1] = blocks[2][2] = blocks[3][1] = blocks[1][2] = color;
                rotateType = ROTATE_3;
                break;
            case 5:
                blocks[2][1] = blocks[2][2] = blocks[3][2] = blocks[1][1] = color;
                rotateType = ROTATE_3;
                break;
            case 6:
                blocks[3][1] = blocks[3][2] = blocks[2][1] = blocks[1][1] = color;
                rotateType = ROTATE_3;
                break;
            case 7:
                blocks[3][1] = blocks[3][2] = blocks[2][2] = blocks[1][2] = color;
                rotateType = ROTATE_3;
                break;
            // 困难难度
            case 8:
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
