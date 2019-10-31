package cn.cz.tetris.game;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Arrays;
import java.util.Random;

public class Piece implements Parcelable {
    public int color;
    public int[][] blocks;

    private Random mRandom;

    public Piece() {
        color = 0;
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

        int pieceType = mRandom.nextInt(5);
        switch (level) {
            case GameConstants.LEVEL_NORMAL:
                break;
            case GameConstants.LEVEL_HARD:
                break;
            case GameConstants.LEVEL_NIGHTMARE:
                break;
        }

        switch (pieceType) {
            // 普通难度
            case 0:
                blocks[2][1] = blocks[2][2] = blocks[3][1] = blocks[3][2] = color;
                break;
            case 1:
                blocks[2][1] = blocks[2][2] = blocks[3][1] = blocks[3][2] = color;
                break;
            case 2:
                blocks[2][1] = blocks[2][2] = blocks[3][1] = blocks[3][2] = color;
                break;
            case 3:
                blocks[2][1] = blocks[2][2] = blocks[3][1] = blocks[3][2] = color;
                break;
            case 4:
                blocks[2][1] = blocks[2][2] = blocks[3][1] = blocks[3][2] = color;
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
