package cn.cz.tetris.game;

import android.os.Parcel;
import android.os.Parcelable;

public abstract class BasePiece implements Parcelable {
    public int color;
    public int[][] blocks;

    public BasePiece() {
        color = 0;
        blocks = new int[GameConstants.PIECE_SIZE][GameConstants.PIECE_SIZE];
    }

    public abstract void initPiece();

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(color);
        int[] ints = new int[GameConstants.PIECE_SIZE * GameConstants.PIECE_SIZE];
        dest.writeIntArray(ints);
    }

    private static void loadParcel(Parcel p, BasePiece b) {
        b.color = p.readInt();
        int[] ints = new int[GameConstants.PIECE_SIZE * GameConstants.PIECE_SIZE];
        p.readIntArray(ints);
    }
}
