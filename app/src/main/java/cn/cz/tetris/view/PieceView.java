package cn.cz.tetris.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

import cn.cz.tetris.game.GameConstants;
import cn.cz.tetris.game.Piece;
import cn.cz.tetris.utils.Utils;

public class PieceView extends View {
    private Piece mPiece;
    private Paint mPaint;
    private int[] mColors;

    public PieceView(Context context) {
        super(context);
        init();
    }

    public PieceView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public PieceView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public void setPiece(Piece piece) {
        mPiece = piece;
        invalidate();
    }

    private void init() {
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mColors = Utils.getColors(getContext());
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (mPiece == null) {
            return;
        }

        for (int i = 0; i != GameConstants.PIECE_SIZE; i++) {
            for (int j = 0; j != GameConstants.PIECE_SIZE; j++) {
                int color = mColors[mPiece.blocks[i][j]];
                if (color != 0) {
                    mPaint.setColor(color);
                }
            }
        }
    }
}
